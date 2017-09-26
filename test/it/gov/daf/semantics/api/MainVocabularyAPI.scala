package it.gov.daf.semantics.api

import it.almawave.linkeddata.kb.utils.JSONHelper

object MainVocabularyAPI extends App {

  val ontoapi = new VocabularyAPI
  ontoapi.start()

  val params = Map("lang" -> "it")

  val json_tree = ontoapi.extract_data(params)

  val json = JSONHelper.writeToString(json_tree)
  println(json)

  // esempio CSV
  //  CSVHelper.create().writeToOutputStream(json_tree, System.out)

  val keys = ontoapi.extract_keys(params)
  println("\n\n\nKEYS? " + keys.mkString(" | "))

  ontoapi.stop()

}

