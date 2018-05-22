package it.almawave.daf.standardization.v1

import it.almawave.linkeddata.kb.catalog.CatalogBox
import org.slf4j.LoggerFactory
import it.almawave.linkeddata.kb.catalog.VocabularyBox
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import it.almawave.linkeddata.kb.catalog.SPARQL
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * This class adds support for the extraction of metadata,
 * needed to run the standardization process for a dataset on a vocabulary.
 */
class StandardizationProcessV1(catalog: CatalogBox) {

  import scala.concurrent.ExecutionContext.Implicits.global
  private val logger = LoggerFactory.getLogger(this.getClass)

  val stdQuery = new StandardizationQueryV1(catalog.conf.getConfig("daf.standardization"))

  // guess the default ontology
  def detect_ontology(voc_box: VocabularyBox) = stdQuery.detect_ontology(voc_box)

  // extracts a list of VocabularyBox
  def vocabulariesWithDependencies(): Seq[VocabularyBox] = catalog.vocabulariesWithDependencies()

  // extract a VocabularyBox by VocabularyID
  def vocabularyWithDependency(vocID: String): Try[VocabularyBox] = Try {
    val res = this.vocabulariesWithDependencies()
      .filter(_.id.equals(vocID))
    if (!res.isEmpty)
      res.head
    else
      throw new RuntimeException(s"vocabulary ${vocID} not found!")
  }

  // extracts the deeper level of hierarchy
  def max_levels(vbox: VocabularyBox): Try[Int] = {
    extract_hierarchies(vbox: VocabularyBox) match {
      case Success(hierarchy) =>
        val max_path = (0 :: hierarchy.map(_.path.size).toList).max
        Success(max_path)
      case Failure(err) => Failure(err) // CHECK: maybe 0?
    }
  }

  /*
   * Extracts hierarchy for a specific VocabularyBox.
   * NOTE: at the moment we decided to use a single RDFs prp* property path query
   * for extracting a single hierarchy: this requires some kind of ordering based on
   * level, thus a description of level/rank.
   * This could be avoided using recursive call on a single property (the one identifying the hierarchy)
   * at RDF4J level, but it's a bit more tricky.
   *
   * We decided to avoid developing it, until we will have at least 3 actual different hierarchies.
   */
  def extract_hierarchies(vbox: VocabularyBox): Try[Seq[Hierarchy]] = Try {

    // TODO: JUNit test for hierarchy!! CHECK: orders of items in path

    // extract the full path / hierarchy to an individual (the uri represents the individual we start from)
    SPARQL(vbox.repo).query(stdQuery.hierarchy(vbox))
      .groupBy(_.getOrElse("uri", "").asInstanceOf[String]).toList
      .sortBy(_._1)
      .map { el =>
        Hierarchy(el._1, el._2.map(_.getOrElse("group", "").asInstanceOf[String]).toList.distinct)
      }

  }

  case class VocabularyStandardizedData(id: String, uri: String, data: Seq[Map[String, Any]])

  // hypothesis for new format...
  def standardizeAllData_v2(lang: String = "it") = {

    //    NOTE: the language is not yet used in this version
    //    TODO: add a filter to the item in the collection using language

    val results: Seq[VocabularyStandardizedData] = this.vocabulariesWithDependencies().map { vbox =>

      vbox.start()

      val _result = Try { this.itemsByVocabularyBox_v2(vbox, lang) } match {

        // TODO: VocabularyStandardizedData

        case Success(items) =>
          VocabularyStandardizedData(vbox.id, vbox.context, items.toList)

        case Failure(err) =>
          logger.debug(s"problem with standardization of ${vbox}")
          VocabularyStandardizedData(vbox.id, vbox.context, List())

      }

      vbox.stop()

      _result

    }

    results.toStream

  }

  @Deprecated
  def standardizeAllData(lang: String = "it") = {

    val results: Seq[VocabularyStandardizedDataV1] = this.vocabulariesWithDependencies().map { vbox =>

      vbox.start()

      val _result = Try { this.standardizeDataByVocabularyBox(vbox, lang) } match {

        case Success(items) =>
          VocabularyStandardizedDataV1(vbox.id, vbox.context, items.toList)

        case Failure(err) =>
          logger.debug(s"problem with standardization of ${vbox}")
          VocabularyStandardizedDataV1(vbox.id, vbox.context, List())

      }

      vbox.stop()

      _result

    }

    results.toStream

  }

  // REFACTORIZATION: IDEA: handling each cell
  def itemsByVocabularyBox_v2(vbox: VocabularyBox, lang: String = "it") = {

    val MAX_LEVELS = this.max_levels(vbox)
    //    logger.debug(s"levels for ${vbox.id}: ${MAX_LEVELS}")

    val hierarchies = extract_hierarchies(vbox).getOrElse(List())
    //    logger.debug(s"extracted hierarchy for: ${vbox.id}\n${hierarchies.mkString("\n")}")

    val ontology_uri = vbox.infer_ontologies().headOption.getOrElse("") // hypothesis: single ontology!
    val obox = catalog.getOntologyByURI(ontology_uri)
    val ontology_id = if (obox.isSuccess) obox.get.id else "SKOS" // WARNING: applying a default!

    val list = hierarchies.map { h =>

      val uri = h.uri
      val path = h.path
      val vocabulary_uri = vbox.meta.url

      val item = SPARQL(vbox.repo).query(s"""
        
        PREFIX clvapit: <https://w3id.org/italia/onto/CLV>
        PREFIX skos: <http://www.w3.org/2004/02/skos/core#> 
        
        SELECT * 
        WHERE {
        
          ?instance_uri a ?concept_uri .
          FILTER(?instance_uri=<${uri}>)
          FILTER(?concept_uri IN (skos:Concept))
          #BIND(REPLACE(STR(?concept_uri), ".*[#/](.*)", "$$1") AS ?concept_id)
          
          {
            ?instance_uri ?property_uri ?property_value .
            FILTER(?property_uri IN (skos:notation, skos:prefLabel, skos:altLabel))
            #BIND(REPLACE(STR(?property_uri), ".*[#/](.*)", "$$1") AS ?property_id)
            BIND(DATATYPE(?property_value) AS ?property_type)
            OPTIONAL { BIND(LANG(?property_value) AS ?property_lang) }
          }  
           
        }  
      """)
        .toList
        .groupBy(_.getOrElse("instance_uri", ""))
        .map { instance =>

          val field_uri = instance._1.toString()
          val fields_list = instance._2.map { m =>

            // expand metadata available for DAF!
            val concept_id = m.getOrElse("concept_uri", "").toString().replaceAll(".*[#/](.*)", "$1")
            val property_id = m.getOrElse("property_uri", "").toString().replaceAll(".*[#/](.*)", "$1")
            val daf_ref = s"${ontology_id}.${concept_id}.${property_id}"

            m - "instance_uri" + ("daf_ref" -> daf_ref)

          }

          fields_list
        }.flatten

      val doc = Map(
        "instance_uri" -> uri,
        "ontology_uri" -> ontology_uri,
        "ontology_id" -> ontology_id,
        "vocabulary_uri" -> vocabulary_uri,
        "hierarchy" -> path,
        "level" -> path.size,
        "data" -> item.toList)

      doc
    }

    list
    //      .groupBy(_.getOrElse("instance_uri", ""))

  }

  // REFACTORIZATION: new idea for standardization...
  def itemsByVocabularyBox_v1(vbox: VocabularyBox, lang: String = "it") = {

    val MAX_LEVELS = this.max_levels(vbox)
    logger.debug(s"levels for ${vbox.id}: ${MAX_LEVELS}")

    val hierarchies = extract_hierarchies(vbox).getOrElse(List())
    logger.debug(s"extracted hierarchy for: ${vbox.id}\n${hierarchies.mkString("\n")}")

    hierarchies.map { h =>

      val uri = h.uri
      val path = h.path

      println(s"\nHierarchy for ${uri}: ${path}")

      val level = path.size

      val item = SPARQL(vbox.repo)
        .query(stdQuery.details(vbox, level, uri, lang))
        .toList.flatMap(_.toList).toList
        // .map(el => (s"${el._1}_level${level}", el._2)) // DISABLED: the hiearchy suggests the level
        .toMap

      val doc = item ++ Map(
        "hierarchy" -> path,
        "level" -> path.size)

      doc
    }

  }

  /*
   * NOTE: this method aims to create a de-normalized, standardized version for the vocabulary/dataset
   */
  @Deprecated
  def standardizeDataByVocabularyBox(vbox: VocabularyBox, lang: String = "it") = {

    // we should know the maximum levels in hierarchies, in order to fill them, if needed
    val MAX_LEVELS = this.max_levels(vbox)

    // 1) the vocabulary is decomposed in a list of hierarchies, each one derived from each leaf
    val hierarchies = extract_hierarchies(vbox).getOrElse(List())
    logger.debug(s"extracted hierarchy for: ${vbox.id}\n${hierarchies.mkString("\n")}")

    // 2) each hierarchy is then expandend with details for each element in the path
    hierarchies.toStream.map { hierarchy =>

      type GROUP_CELLS = List[(String, Any)] // TODO: parse to case class!

      val future_with_cells: Seq[Future[StdRow]] = hierarchy.path
        .zipWithIndex
        .map {

          case (uri, l) =>

            val level = l + 1

            // we use a Future here to try improving performances when collecting results from many SPARQL queries
            val fut_group: Future[List[StdCell]] = Future {

              val group_cells: GROUP_CELLS = SPARQL(vbox.repo)
                .query(stdQuery.details(vbox, level, uri, lang))
                .toList.flatMap(_.toList).toList
                .map(el => (s"${el._1}_level${level}", el._2))

              // actual fields (no internal metadata)
              val fields = group_cells.toList.map(_._1).toList
                .filter(_.contains("_level"))
                .filterNot(_.contains("_meta"))
                .filterNot(_.contains("_type"))

              // results as Map
              val map = group_cells.toMap

              /*
               *  each record has a number of pseudo-columns, used to add internal metadata informations:
               *  we can parse them into a proper structured Cell object, in order to have all the data
               *  available for further flexible processing
               */
              fields.map { field_name =>

                // let's extract the data & metadata for each cell
                val field_value = map.getOrElse(field_name, "").asInstanceOf[Object]
                val field_datatype = map.getOrElse(s"_type_${field_name}", "String").asInstanceOf[Object]
                val field_meta1 = map.getOrElse(s"_meta1_${field_name}", "UnknownOntology.UnknownConcept.unkownProperty").asInstanceOf[String]
                val field_meta2 = group_cells.filter(_._1.contains("_meta2")).headOption.map(_._2).getOrElse(s"UnknownVocabulary.level${level}").asInstanceOf[String]

                StdCell(
                  uri,
                  field_name,
                  field_value,
                  field_datatype,
                  vbox.id,
                  hierarchy.path, // REVIEW this hierarchy path!
                  field_meta1,
                  field_meta2)
              }

            }

            fut_group

          // CHECK: lookup of property into the existing ontologies! -> OntologyID.ConceptID.propertyID
          // CHECK: FILL: List.fill(MAX_LEVELS - _details.size)(null)

        }
        .toStream

      // List[Future] -> Future[List]
      // la future contiene una lista di gruppi di celle, da ri-organizzare prima dell'output!
      val futs_seq: Future[Seq[StdCell]] = Future.sequence(future_with_cells)
        .map { el => el.flatMap(_.toList).sortBy { cell => cell.uri } }

      // awaiting all the computed futures
      Await.result(futs_seq, Duration.Inf)
    }

  }

  // MODELS ....................................................................
  case class Hierarchy(uri: String, path: List[String])

  type StdRow = Seq[StdCell]

  // TODO: move elesewhere
  case class StdCell(
    uri:          String,
    name:         String,
    value:        Object,
    datatype:     Object,
    vocabularyID: String,
    hierarchy:    Seq[String],
    meta_level1:  String,
    meta_level2:  String)

  object StdCell {
    val EMPTY = StdCell(
      "uri://unknown",
      "", "",
      "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString",
      "UnknownVocabulary",
      Array[String](),
      "UnknowOntology.UnknownConcept.unkownProperty", "UnknownVocabulary.level1")
  }

  case class VocabularyStandardizedDataV1(
    vocabularyID: String,
    context:      String,
    data:         Seq[StdRow])

  // MODELS ....................................................................

}