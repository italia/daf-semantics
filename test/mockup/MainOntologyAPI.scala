package mockup

import java.io.File
import it.almawave.linkeddata.kb.repo.RDFRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import it.almawave.linkeddata.kb.repo.RDFRepositoryBase
import it.almawave.linkeddata.kb.utils.JSONHelper
import it.gov.daf.semantics.api.OntologyAPI
import com.typesafe.config.ConfigFactory

object MainOntologyAPI extends App {

  val params = Map("lang" -> "it")

  val oapi = new OntologyAPI()
  oapi.start()

  //  val results = oapi.extract_hierarchy(params)
  //  val json = JSONHelper.writeToString(results)
  //  println(json)

  val fields = List("CLV-AP_IT_Country_name", "CLV-AP_IT_City_name", "CLV-AP_IT_Province_name", "CLV-AP_IT_Region_name")
  val hierarchy_props = oapi.extract_hierarchy_properties(params, fields)

  val json = JSONHelper.writeToString(hierarchy_props)
  println(json)

  oapi.stop()

}
