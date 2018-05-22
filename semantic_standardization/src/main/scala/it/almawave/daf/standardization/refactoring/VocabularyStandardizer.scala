package it.almawave.daf.standardization.refactoring

import it.almawave.linkeddata.kb.catalog.VocabularyBox
import it.almawave.linkeddata.kb.catalog.SPARQL
import it.almawave.linkeddata.kb.utils.ModelAdapter
import java.io.OutputStream

/**
 * a class supporting DAF-standardization process for a VocabularyBox
 */
class VocabularyStandardizer(val vbox: VocabularyBox) {

  type CELLS_GROUP = Seq[CELL]

  def start = vbox.start()
  def stop = vbox.stop()

  //  def _cells = this.prepare_data()

  lazy val MAX_COLS = this.prepare_data().map(_.flatten).map(_.size).max

  /*
   * gets the URIs for all the instances
   */
  def extract_instances_uri(): Seq[String] = {

    this.extract_parents().map(_.instance_uri).toStream

  }

  /*
   * gets the element/parent relations
   */
  def extract_parents(): Seq[ElementWithParent] = {

    val concepts = List("skos:Concept") // REVIEW
    val query_instances = QueryStandardization.q_instances(concepts)
    SPARQL(vbox.repo).query(query_instances).toStream
      .map { item => ModelAdapter.fromMap[ElementWithParent](item) }

  }

  /*
   * expand the details for an element (given its URI), using a common query
   */
  def expand_details_by_uri(uri: String): CELLS_GROUP = {
    val lang = "it"
    val group = SPARQL(vbox.repo).query(QueryStandardization.q_details(uri)(lang)).toStream
      .map { doc => // add ontology/vocabulary references
        val concept_uri = doc.getOrElse("concept_uri", "").toString()
        val ontology_uri = lookupOntologyByConcept(concept_uri)
        doc + ("ontology_uri" -> ontology_uri) + ("vocabulary_uri" -> vbox.context)
      }
      .map(ModelAdapter.fromMap[CELL](_))
      .distinct

    group
  }

  /*
   * expand all the parsed data as tree: this data structure could be useful for JSON export
   * TODO: add default Ontology ID/URI to a vocabulary
   */
  def toJSONTree() = {

    val metadata = this.getMetadata()

    // TODO: automatically extract ontology!

    val data = this.prepare_data

    val ontologies_list = data.flatMap(_.flatMap(_.cells.map { el =>
      (el.ontology_uri.replaceAll(".*[#/](.*)", "$1"), el.ontology_uri)
    })).head
    // CHECK: .headOption.getOrElse(("SKOS", "http://www.w3.org/2004/02/skos/core")) // assume SKOS by default

    val ontologyID = ontologies_list._1
    val ontologyURI = ontologies_list._2

    VocabularyStadardizedData(
      vbox.id, vbox.meta.url.toURI(),
      ontologyID, ontologyURI, // consider using Option
      metadata,
      data)

  }

  def lookupOntologyByConcept(concept_uri: String): String = {
    vbox.meta.dependencies.filter(concept_uri.contains(_))
      .headOption.getOrElse("http://www.w3.org/2004/02/skos/core#") // assume SKOS by default
  }

  /**
   * this method creates a list of conventional name for fields in the tabluar representation
   */
  def prepare_fields_name: Seq[String] = {
    this.prepare_data()
      .find(row => row.map { group => group.size }.sum == MAX_COLS)
      .map {
        _.zipWithIndex
          .map {
            case (group, g) =>
              group.map { cell =>
                val name = cell.property_uri.replaceAll("^.*[#/](.*)$", "$1")
                val level = "%02d".format(g + 1)
                val lang = {
                  val L = cell.property_lang.getOrElse("").trim
                  if (L.equals("")) "" else s"_${L}"
                }
                s"${name}__${level}${lang}"
              }
          }
          .flatten
      }.getOrElse(List())
  }

  def getMetadata() = {

    //    val fields = this.prepare_fields_name

    // TODO: automatically extract ontology!

    val fields = prepare_fields_name

    val data = this.prepare_data
      .filter(_.map(_.size).sum == MAX_COLS)
      .head.flatten

    val fields_metadata = fields.zip(data)
      .map {
        case (field, cell) =>

          val propertyType = cell.property_type
          val propertylang = cell.property_lang

          val propertyURI = cell.property_uri
          val propertyID = propertyURI.replaceAll(".*[#/](.*)", "$1")

          val conceptURI = cell.concept_uri
          val conceptID = cell.concept_uri.replaceAll(".*[#/](.*)", "$1")
          val vocabularyURI = cell.vocabulary_uri
          val vocabularyID = vocabularyURI.replaceAll(".*[#/](.*)", "$1")
          val ontologyURI = cell.ontology_uri
          val ontologyID = ontologyURI.replaceAll(".*[#/](.*)", "$1")

          val DAFAnnotation = s"""${ontologyID}.${conceptID}.${propertyID}"""

          (field ->
            CellMetadata(
              field,
              DAFAnnotation,
              propertyType,
              propertylang,
              propertyID,
              propertyURI,
              conceptID,
              conceptURI,
              vocabularyID,
              vocabularyURI,
              ontologyID,
              ontologyURI))

      }.toMap

    fields_metadata
  }

  /*
   * TODO: refactorize with a dedicated object!
   */
  def toCSV()(implicit out: OutputStream) {

    // CHECK: usage of a default outputstream for String output

    val SEP = ";"
    val DEL = "\""
    val EMPTY_CELL = ""

    /*
     * currently the datatypes are handled by URI, while the actual data are always String.
     * NOTE: in case we maintain the native RDF4j datatypes, we should refactorize the code here
     */
    def prepare_csv_value(_value: Object, _type: Option[String]): Object = {

      // REVIEW / REWRITE datatypes...

      if (_type.isDefined) {

        val literal_type = _type.get

        if (literal_type.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString") ||
          literal_type.equals("http://www.w3.org/2001/XMLSchema#string"))

          DEL + _value + DEL

        else

          _value

      } else {
        _value
      }

    }

    // preparing headers
    def prepare_csv_header = this.prepare_fields_name

    // preparing actual data on cells
    def prepare_csv_data = {
      this.prepare_data().map { row =>
        row.flatten.map { item =>
          val _type = item.property_type
          prepare_csv_value(item.property_value, _type) // HACK!
        }
      }
        .map { item => item ++ List.fill(MAX_COLS - item.size)(EMPTY_CELL) }
    }

    // WRITE CSV on outputstream
    (Stream(prepare_csv_header) ++ prepare_csv_data)
      .foreach { row =>
        out.write(row.mkString(SEP).getBytes)
        out.write("\n".getBytes)
        row
      }
    out.flush()

  }

  /*
   * This method executes the standard queries in order to construct an in-memory representation
   * of the data for this vocabulary.
   * NOTE:
   * 	each query will produce a set of common field (key/value), at each level of the taxonomy,
   * 	in order to be able to eventually produce two different output from here: the CSV / tabular export
   * 	for the actual data values, and a JSON / tree representation of metadata associated to the fields.
   * 	So if we have N fields and K levels, we will expect to have N*K columns when exporting the final table.
   * 	A group of values (the tuple for a level) is handled as a list, so for each potential row we will have
   * 	a list of list of CELL.
   */
  def prepare_data(): Seq[Seq[CellGroup]] = {

    // REVIEW HERE
    this.extract_rows_reduced(true)
      .map { h =>
        h.path.map { uri =>
          new CellGroup(uri, this.expand_details_by_uri(uri): _*)
        }
      }.toStream

  }

  /*
   * this method should get all the pre-calculated hierarchies, then reduce the list to the leaves paths
   *
   * FIX
   */
  def extract_rows_reduced(reduce: Boolean = true): Seq[StdHierarchy] = {

    val hierarchies = this.extract_hierarchies().toList

    val before = hierarchies.size

    // TODO
    val list = hierarchies

    //    REVIEW
    //      .foldLeft(Stream[StdHierarchy]()) { (list, element) =>
    //
    //        if (reduce && list.find(el => el.path.containsSlice(element.path)).isDefined)
    //          Stream(element)
    //        else
    //          list ++ Stream(element)
    //
    //      }

    val after = list.size

    //    println("REDUCED SIZE? ", before, after)

    list
  }

  /*
   * this method creates a list of hierarchy/path for each element
   *
   * CHECK: move this method directly in the VBox, if possible!
   */
  def extract_hierarchies(): Seq[StdHierarchy] = {

    def _instances: Seq[ElementWithParent] = extract_parents().toStream

    // CHECK: consider using foldLeft here...
    def path_of(element: ElementWithParent): Seq[ElementWithParent] = {
      _instances
        .find(_.instance_uri.equals(element.parent_uri.getOrElse(""))) match {
          case None         => Stream(element)
          case Some(parent) => path_of(parent) ++ Stream(element)
        }
    }

    _instances
      .map { pair =>
        val path = path_of(pair).map { item => item.instance_uri }
        StdHierarchy(pair.instance_uri, path.toList)
      }

  }

}