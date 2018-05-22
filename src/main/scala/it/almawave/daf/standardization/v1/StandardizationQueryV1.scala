package it.almawave.daf.standardization.v1

import com.typesafe.config.Config
import java.nio.file.Paths
import java.nio.file.Files
import it.almawave.linkeddata.kb.catalog.VocabularyBox
import java.io.FileFilter
import java.io.File
import java.nio.file.Path
import org.slf4j.LoggerFactory

/*
 * this is an helper class for handling the query pairs needed for the standardization datasets
 */
class StandardizationQueryV1(conf: Config) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val config_dir = Paths.get(conf.root().origin().filename()).toFile().getParent
  val queries_dir = conf.getString("queries.dir")

  //  val default_query_hierarchy: String = resolve_query(conf.getString("query.hierarchy"))
  //  val default_query_details: String = resolve_query(conf.getString("query.details"))
  //def resolve_query(q_conf: String): String = {
  //  val q_hierarchy_path = Paths.get(config_dir, q_conf).normalize().toAbsolutePath()
  //  new String(Files.readAllBytes(q_hierarchy_path))
  //}

  val default_query_hierarchy: Path = Paths.get(config_dir, conf.getString("query.hierarchy")).normalize().toAbsolutePath()
  val default_query_details: Path = Paths.get(config_dir, conf.getString("query.details")).normalize().toAbsolutePath()

  val queries = enumerateSPARQLQueries()

  def enumerateSPARQLQueries() = {
    Paths.get(config_dir, queries_dir).normalize().toFile()
      .listFiles(new FileFilter {
        override def accept(file: File): Boolean = file.toString().endsWith("sparql")
      }).toStream
  }

  // get the file with the hierarchy query for a given id
  def hierarchyQueryFile(id: String): Option[File] = queries
    .filter { f => f.getParent.equalsIgnoreCase(id) }
    .filter(_.getName.startsWith("hierarchy"))
    .headOption

  // get the file with the details query for a given id
  def detailsQueryFile(id: String): Option[File] = queries
    .filter { f => f.getParent.equalsIgnoreCase(id) }
    .filter(_.getName.startsWith("details"))
    .headOption

  /*
   *  guess the default ontology
   *  NOTE: this should be once for all at vocabulary level, avoiding multiple requests
   */
  def detect_ontology(voc_box: VocabularyBox) = {

    val ontos = voc_box.infer_ontologies()

//    println("ontologies inferred.........................")
//    println(ontos.mkString("\n"))
//    println("ontologies inferred.........................")

    ontos.map(_.replaceAll(".*[#/](.*)", "$1"))
      .headOption.getOrElse("SKOS")
  }

  /**
   * retrieving the query used for hierarchy extraction
   */
  def hierarchy(voc_box: VocabularyBox) = {

    val onto_id = detect_ontology(voc_box)

    val query_path: Path = hierarchyQueryFile(onto_id)
      .map(_.toPath())
      .getOrElse(default_query_hierarchy)

    logger.debug(s"daf.standardization> try ${voc_box.id} with hierarchy query: ${query_path}")

    new String(Files.readAllBytes(query_path))

  }

  /**
   * This method internally uses a query to expand details about an individual.
   * We need to use some convention, in order to handle the metadata for internal usage:
   *  + _type_{field}
   *  	is used for describing the datatype for each cell/column, inferred from SPARQL/RDF.
   *  	TODO: conversion of the RDF4J datatypes to a selection of meaningful Java corrispondences
   * 	+ _meta1_{field}
   * 		is used for the metadata level 1, eg: `SKOS.Concept.notation`
   * 	+ _meta2 is actually _meta2_{field}
   * 		is used for the metadata level 2, eg: `Licenze.level1`
   */
  def details(voc_box: VocabularyBox, level: Int, uri: String, lang: String) = {

    val onto_id = detect_ontology(voc_box)

    val query_path: Path = detailsQueryFile(onto_id)
      .map(_.toPath())
      .getOrElse(default_query_details)

    // disabled for too many logs! logger.debug(s"daf.standardization> try ${voc_box.id} with details query: ${query_path}")

    val query = new String(Files.readAllBytes(query_path))
    query
      .replace("${vocabularyID}", voc_box.id)
      .replace("${level}", level.toString())
      .replace("${uri}", uri)
      .replace("${lang}", lang)

  }

}