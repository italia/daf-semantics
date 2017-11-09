package it.gov.daf.semantics.api

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory
import com.typesafe.config.Config
import java.io.File

import it.almawave.linkeddata.kb.repo.utils.ConfigHelper
//import it.almawave.linkeddata.kb.utils.JSONHelper

/**
 * TODO: extract JUnit from here
 */
object MainOntologyAPI extends App {

  val params = Map("lang" -> "it")

  val ontoapi_factory = new OntologyAPIFactory()

  val conf = OntologyAPIFactory.DEFAULT_CONFIG

  val TEST_CONFIG = ConfigHelper.injectParameter(conf, "data_dir", "./dist/data")
  println("TEST_CONFIG\n" + TEST_CONFIG)

  ontoapi_factory.config(TEST_CONFIG)
  ontoapi_factory.start()

  val oapi = ontoapi_factory.items("clvapit")

  //  TEST
  val results = oapi.extract_hierarchy(params)
  //  val hjson = JSONHelper.writeToString(results)
  //  println("\n\nEXTRACTED HIERARCHY" + hjson)
  println("\n\nEXTRACTED HIERARCHY" + results)

  val fields = List(
    "CLV-AP_IT.Country.name",
    "CLV-AP_IT.City.name",
    "CLV-AP_IT.Province.name",
    "CLV-AP_IT.Region.name")

  val hierarchy_props = oapi.extract_hierarchy_properties(params, fields)
  println("\n\nEXTRACTED HIERARCHIES\n" + hierarchy_props.mkString("\n"))

  // ###########################################################################################

  println("\n##########################################")
  println("POI-AP_IT EXAMPLE")

  //  val fields = List("POI-AP_IT.POICategoryClassification.POICategoryName")

  val oapi_poi = ontoapi_factory.items("poiapit")
  val poi_results = oapi_poi.extract_hierarchy(params)
  println("HIERARCHY ELEMENTS\n" + poi_results.mkString("\n"))

//  val poi_hierarchy_props = oapi.extract_hierarchy_properties(
//    params,
//    List("POI-AP_IT.POICategoryClassification.POICategoryName"))
//  println("\n\nEXTRACTED HIERARCHIES\n" + poi_hierarchy_props.mkString("\n"))

  // ###########################################################################################
  ontoapi_factory.stop()

}
