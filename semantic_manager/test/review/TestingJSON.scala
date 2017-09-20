package review

import scala.io.Source
import utilities.JSONHelper

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import play.api.libs.json.JsDefined
import play.api.libs.json.Json

import semantic_manager.yaml.OntologyMeta

// package yaml {
//  object OntologyAdapter {
//    implicit val ontoJsonFormat = Json.format[OntologyMeta]
//  }
//  }

object TestingJSON extends App {

  val body = Source.fromURL("http://localhost:8000/stanbol/ontonethub/ontologies")("UTF-8").mkString
  
  
  implicit val ontoJsonFormat = Json.format[OntologyMeta]
  val ontos = Json.parse(body).as[List[OntologyMeta]]
  println(ontos.mkString("\n"))

  println("------------------------------------------------------------------------------")

  case class User(first_name: String, second_name: String)
  //  object User {
  //    implicit val userJsonFormat = Json.format[User]
  //  }

  implicit val userJsonFormat = Json.format[User]

  val txt = """[ {"first_name":"Mario","second_name":"Rossi"}, {"first_name":"Luigi","second_name":"Bianchi"} ]"""
  val users = Json.parse(txt).as[List[User]]
  println(users)

  //------------
  //  val json_list = list.map {
  //    it => Json.reads[OntologyMeta].reads(it.result.get)
  //      
  //  }
  //  println(json_list.mkString("\n"))

}