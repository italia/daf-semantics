package it.gov.daf.semantics.api

import com.typesafe.config.ConfigFactory
import it.almawave.linkeddata.kb.repo.RDFRepository
import java.io.File
import com.typesafe.config.Config
import org.eclipse.rdf4j.rio.Rio
import java.nio.file.Paths
import scala.util.Try
import scala.io.Source
import play.Logger
import it.almawave.linkeddata.kb.utils.JSONHelper

class OntologyAPI(conf: Config = ConfigFactory.empty()) {

  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters._

  val logger = Logger.underlying()

  // TODO: switch to externalized dependency for `it.almawave.linkeddata.kb.repo.RDFRepository`
  val repo = RDFRepository.memory()

  val onto_name = conf.getString("ontology.name")

  val query_template = readFile(conf.getString("ontology.query.hierarchy"))
  //  println("QUERY_TEMPLATE: " + query_template)

  def start() {

    logger.debug(s"starting OntologyAPI(${onto_name})")
    repo.start()

    // clear the underlying store
    val contexts = conf.getStringList("ontology.contexts")
    repo.store.clear(contexts: _*)

    // loading data for the vocabulary dataset
    load_ontology_file()

  }

  def stop() {
    repo.stop()
    logger.debug(s"stopping OntologyAPI(${onto_name})")
  }

  // loads ontology file
  private def load_ontology_file() {

    val fileName = conf.getString("ontology.file")
    val mime = Rio.getParserFormatForFileName(fileName).get.getDefaultMIMEType
    val rdf_file = Paths.get(fileName).normalize().toAbsolutePath().toFile()

    val contexts = conf.getStringList("ontology.contexts")

    logger.debug(s"loading vocabulary ${onto_name} in ${contexts.mkString(" | ")}")
    repo.io.addRDFFile(rdf_file, mime, contexts: _*)

  }

  // TODO: directly using Config instead of Map
  def parse_query(parameters: Map[String, Object]) = {
    val flat_config = conf.entrySet().map(p => (p.getKey, p.getValue.unwrapped())).toMap
    val map: Map[String, Object] = flat_config ++ parameters
    this.injectParameters(query_template, map)
  }
  private def query_placeholder(name: String) = """${""" + name + """}"""

  /*
   * refactorize to more general parameters handler, importing the Configuration library
   */
  private def injectParameters(query_template: String, map: Map[String, Object]) = Try {
    // TODO: refactorize avoiding string if possible
    var txt = query_template
    map.foreach { p =>
      txt = txt.replace(query_placeholder(p._1), p._2.toString())
    }
    txt
  }

  def extract_hierarchy(parameters: Map[String, Object] = Map.empty) = {

    val query = parse_query(parameters).get

    repo.sparql
      .query(query).get
      .toList

  }

  def extract_hierarchy_properties(
    parameters: Map[String, Object],
    properties: List[String] = List()): List[Map[String, Object]] = {

    val hierarchy_elements = extract_hierarchy(parameters)
      .sortBy { map => map.get("rank").get.asInstanceOf[String] }
      .map { map => map.get("id").get.asInstanceOf[String] }

    // TODO: fix the _ with .
    properties.map { prop =>

      // hack for preserving correct naming conventions
      val prop_path = (onto_name + prop.substring(prop.indexOf(onto_name) + onto_name.length()).replace("_", ".")).split("\\.")

      val prop_name = prop_path(prop_path.size - 1)
      val concept_name = prop_path(prop_path.size - 2)
      val concept_hierarchy = hierarchy_elements.slice(0, hierarchy_elements.indexOf(concept_name) + 1)

      // recostruct the related part of the hierarchy
      val hierarchy_tree: List[Map[String, Any]] = concept_hierarchy
        .zipWithIndex
        .map { item => Map("class" -> item._1, "level" -> item._2) }

      Map(
        "vocabulary" -> onto_name,
        "path" -> prop_path.mkString("."),
        "hierarchy_flat" -> concept_hierarchy.mkString("."),
        "hierarchy" -> hierarchy_tree.toList)

    }

  }

  private def readFile(filePath: String) = {
    val dataset_path = Paths.get(filePath).toAbsolutePath()
    val src = Source.fromFile(dataset_path.toFile(), "UTF-8")
    val content = src.getLines().mkString("\n")
    src.close()
    content
  }

}

class OntologyAPIFactory(config: Config = ConfigFactory.empty()) {

  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters._

  val logger = Logger.underlying()

  private var conf = config

  // NOTE: for the prototype there is only an instance!
  var items: Map[String, OntologyAPI] = Map()

  def config(config: Config) {

    // overriding / merging configurations
    conf = config.withFallback(conf).resolve()

  }

  def start() {

    logger.debug(s"starting OntologyAPIFactory Factory)")

    // initializing each micro-repository
    // getting the list of configured ontologies
    val names = conf.root().keySet().toList
    items = names.map { name => (name, new OntologyAPI(conf.getConfig(name))) }.toMap
    // starting each micro-repository
    items.foreach { _._2.start() }

    // overriding / merging configurations
    conf = config.withFallback(conf)

  }

  def stop() {

    logger.debug(s"stopping OntologyAPIFactory Factory)")
    // stopping each micro-repository
    items.foreach { _._2.stop() }

  }

}

object OntologyAPIFactory {

  // NOTE: this is only a prototype!!
  // TODO: load from file! 
  val DEFAULT_CONFIG = ConfigFactory.parseString("""
  
  "data_dir": "./data"
  
  clvapit {
  
    ontology.name: "CLV-AP_IT"
		ontology.prefix: "clvapit"
    
    ontology.file: ${data_dir}"/ontologies/agid/CLV-AP_IT/CLV-AP_IT.ttl"
    
    ontology.contexts: [ "http://dati.gov.it/onto/clvapit#" ]
        
    ontology.query.hierarchy: ${data_dir}"/ontologies/agid/CLV-AP_IT/CLV-AP_IT.hierarchy.sparql"

  }

  """)

}