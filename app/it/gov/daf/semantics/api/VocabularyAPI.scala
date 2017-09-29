package it.gov.daf.semantics.api

import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import it.almawave.linkeddata.kb.repo.RDFRepository
import play.Logger
import scala.io.Source
import java.nio.file.Paths
import org.eclipse.rdf4j.rio.Rio
import scala.util.Try
import it.almawave.linkeddata.kb.utils.JSONHelper

/*
 * NOTE: at the moment this is only a prototype!
 */
class VocabularyAPI(conf: Config = ConfigFactory.empty()) {

  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters._

  // TODO: externalize!
  val repo = RDFRepository.memory()
  //  val logger = LoggerFactory.getLogger(this.getClass)
  val logger = Logger.underlying()

  val voc_name = conf.getString("vocabulary.name")
  val query_template = readFile(conf.getString("vocabulary.query.csv"))

  def start() {

    repo.start()
    logger.debug(s"starting VocabularyAPI(${voc_name})")

    // clear the underlying store
    val contexts = conf.getStringList("vocabulary.contexts")
    repo.store.clear(contexts: _*)

    // loading data for the vocabulary dataset
    load_dataset_file()

  }

  def stop() {

    repo.stop()
    logger.debug(s"stopping VocabularyAPI(${voc_name})")

  }

  private def readFile(filePath: String) = {
    val dataset_path = Paths.get(filePath).normalize().toAbsolutePath()
    val src = Source.fromFile(dataset_path.toFile(), "UTF-8")
    val content = src.getLines().mkString("\n")
    src.close()
    content
  }

  // TODO: directly using Config instead of Map
  def parse_query(parameters: Map[String, Object]) = {
    val flat_config = conf.entrySet().map(p => (p.getKey, p.getValue.unwrapped())).toMap
    val map: Map[String, Object] = flat_config ++ parameters
    this.injectParameters(query_template, map)
  }

  // load vocabulary file
  private def load_dataset_file() {
    val fileName = conf.getString("vocabulary.file")
    val mime = Rio.getParserFormatForFileName(fileName).get.getDefaultMIMEType
    val luoghi_file = Paths.get(fileName).normalize().toAbsolutePath().toFile()
    val contexts = conf.getStringList("vocabulary.contexts")
    logger.debug(s"loading vocabulary ${voc_name} in ${contexts.mkString(" | ")}")
    repo.io.addRDFFile(luoghi_file, mime, contexts: _*)
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

  def extract_data(parameters: Map[String, Object] = Map.empty) = {

    val oname = conf.getString("vocabulary.ontology.name")

    extract_data_map(parameters).map {
      _.map { el =>
        val key = el._1
        val value = el._2
        val path = oname + key.substring(key.indexOf(oname) + oname.length()).replace("_", ".")
        (key, value)
      }.toMap
    }

  }

  def extract_data_map(parameters: Map[String, Object] = Map.empty) = {

    val query = parse_query(parameters).get

    val oname = conf.getString("vocabulary.ontology.name")
    val oprefix = conf.getString("vocabulary.ontology.prefix")

    repo.sparql
      .query(query).get
      .toStream
      .map {
        // fixing name
        _.toList.map { el => (el._1.replace(oprefix, oname), el._2) }.toMap
      }
      .toList

  }

  def extract_keys(parameters: Map[String, Object] = Map.empty) = {
    extract_data_map(parameters).flatMap { item => item.keySet }.distinct
  }

}

class VocabularyAPIFactory(config: Config = ConfigFactory.empty()) {

  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters._

  val logger = Logger.underlying()

  private var conf = config

  // NOTE: for the prototype there is only an instance!
  var items: Map[String, VocabularyAPI] = Map()

  def config(config: Config) {

    // overriding / merging configurations
    conf = config.withFallback(conf).resolve()

  }

  def start() {

    logger.debug(s"starting VocabularyAPI Factory)")

    // getting the list of configured vocabularies
    val names = conf.root().keySet().toList
    // initializing each micro-repository
    items = names.map { name => (name, new VocabularyAPI(conf.getConfig(name))) }.toMap
    // starting each micro-repository
    items.foreach { _._2.start() }

  }

  def stop() {

    logger.debug(s"stopping VocabularyAPI Factory)")
    // stopping each micro-repository
    items.foreach { _._2.stop() }

  }

}

object VocabularyAPIFactory {

  // NOTE: this is only a prototype!!
  // TODO: load from file! 
  val DEFAULT_CONFIG = ConfigFactory.parseString("""
  
  "data_dir": "./data"
  
  "Istat-Classificazione-08-Territorio" {
  
    vocabulary.name: "Istat-Classificazione-08-Territorio"
		
		vocabulary.ontology.name: "CLV-AP_IT"
		vocabulary.ontology.prefix: "clvapit"
    
    vocabulary.file: ${data_dir}"/vocabularies/Istat-Classificazione-08-Territorio.ttl"
    # mime: "text/turtle"
    vocabulary.contexts: [ "http://dati.gov.it/onto/clvapit#" ]
        
    vocabulary.query.csv: ${data_dir}"/vocabularies/Istat-Classificazione-08-Territorio#dataset.csv.sparql"

  }

  """)

}