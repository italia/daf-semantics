package it.gov.daf.semantics.api

import it.almawave.linkeddata.kb.utils.JSONHelper
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory
import com.typesafe.config.Config
import it.almawave.linkeddata.kb.repo.utils.ConfigHelper

object MainOntologyAPI extends App {

  val params = Map("lang" -> "it")

  val factory = new OntologyAPIFactory()

  val TEST_CONFIG = ConfigHelper.injectParameter(OntologyAPIFactory.DEFAULT_CONFIG, "data_dir", "./dist/data")
  factory.config(TEST_CONFIG)

  factory.start()

  val oapi = factory.items("clvapit")

  //  TEST
  //  val results = oapi.extract_hierarchy(params)
  //  val hjson = JSONHelper.writeToString(results)
  //  println(hjson)

  val fields = List("CLV-AP_IT_Country_name", "CLV-AP_IT_City_name", "CLV-AP_IT_Province_name", "CLV-AP_IT_Region_name")
  val hierarchy_props = oapi.extract_hierarchy_properties(params, fields)

  val json = JSONHelper.writeToString(hierarchy_props)
  println(json)

  factory.stop()

}
