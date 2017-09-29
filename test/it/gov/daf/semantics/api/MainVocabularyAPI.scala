package it.gov.daf.semantics.api

import it.almawave.linkeddata.kb.utils.JSONHelper
import org.slf4j.LoggerFactory
import play.Logger
import com.typesafe.config.ConfigFactory

object MainVocabularyAPI extends App {

  val logger = Logger.underlying()

  val ontofactory = new VocabularyAPIFactory(TEST_CONFIG)
  ontofactory.start()

  val ontoapi = ontofactory.items("Istat-Classificazione-08-Territorio")

  val params = Map("lang" -> "it")

  val json_tree = ontoapi.extract_data(params)
  //  val json = JSONHelper.writeToString(json_tree)
  //  logger.debug(json)

  val results = json_tree.map {
    _.toList.map { item =>
      ("key" -> item._1, "value" -> item._2.toString())
    }.toSeq
  }.toSeq

  val json_results = JSONHelper.writeToString(results)
  logger.debug(json_results)

  val keys = ontoapi.extract_keys(params)
  logger.debug("\n\nKEYS: {}", keys.mkString(" | "))

  ontofactory.stop()

  def TEST_CONFIG = ConfigFactory.parseString("""
  
  Istat-Classificazione-08-Territorio {
  
    vocabulary.name: "Istat-Classificazione-08-Territorio"
		
		vocabulary.ontology.name: "CLV-AP_IT"
		vocabulary.ontology.prefix: "clvapit"
    
    vocabulary.file: "./dist/data/vocabularies/Istat-Classificazione-08-Territorio.ttl"
    # mime: "text/turtle"
    vocabulary.contexts: [ "http://dati.gov.it/onto/clvapit#" ]
        
    vocabulary.query.csv: "./dist/data/vocabularies/Istat-Classificazione-08-Territorio#dataset.csv.sparql"

  }""")

}

