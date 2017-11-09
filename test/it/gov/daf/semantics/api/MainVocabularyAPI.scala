package it.gov.daf.semantics.api

import org.slf4j.LoggerFactory
import play.Logger
import com.typesafe.config.ConfigFactory
import it.almawave.linkeddata.kb.repo.utils.ConfigHelper
import it.almawave.linkeddata.kb.utils.JSONHelper

object MainVocabularyAPI extends App {

  val params = Map("lang" -> "it")

  val logger = Logger.underlying()

  val voc_factory = new VocabularyAPIFactory()

  //  val config = ConfigHelper.load("conf/vocabularies_api.conf")

  val TEST_CONFIG = ConfigHelper.injectParameter(VocabularyAPIFactory.DEFAULT_CONFIG, "data_dir", "./dist/data")

  voc_factory.config(TEST_CONFIG)

  voc_factory.start()

  //  println("\nconfigured vocabulary datasets:")
  //  println(voc_factory.items.mkString("\n"))
  //  println("\n\n")

  //  val vocapi = voc_factory.items("Istat-Classificazione-08-Territorio")
  val vocapi = voc_factory.items("POICategoryClassification")

  val json_tree = vocapi.extract_data(params)
  //  val json = JSONHelper.writeToString(json_tree)
  //  logger.debug("JSON\n" + json)
  println("json_tree: " + json_tree)

  val results = json_tree.map {
    _.toList.map { item =>
      ("key" -> item._1, "value" -> item._2.toString())
    }.toSeq
  }.toSeq.slice(0, 2)

  val keys = vocapi.extract_keys(params)
  logger.debug("\n\nKEYS:\n{}", keys.mkString(" | "))

  logger.debug("JSON RESULTS: ")
  results.foreach { result =>
    //    val json_result = JSONHelper.writeToString(result)
    //    logger.debug(json_result)
    println("json_result: " + result)
  }

  voc_factory.stop()
}

