package it.gov.daf.semantics.api

import it.almawave.linkeddata.kb.utils.JSONHelper
import com.typesafe.config.ConfigFactory

object MainOntologyAPI extends App {

  val params = Map("lang" -> "it")

  val factory = new OntologyAPIFactory(TEST_CONFIG)
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

  def TEST_CONFIG = ConfigFactory.parseString("""
  
  clvapit {
  
    ontology.name: "CLV-AP_IT"
		ontology.prefix: "clvapit"
    
    ontology.file: "./dist/data/ontologies/agid/CLV-AP_IT/CLV-AP_IT.ttl"
    
    ontology.contexts: [ "http://dati.gov.it/onto/clvapit#" ]
        
    ontology.query.hierarchy: "./dist/data/ontologies/agid/CLV-AP_IT/CLV-AP_IT.hierarchy.sparql"

  }

  """)

}
