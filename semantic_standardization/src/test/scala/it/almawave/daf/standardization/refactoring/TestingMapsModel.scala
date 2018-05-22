package it.almawave.daf.standardization.refactoring

import java.net.URI
import scala.reflect._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.beans.BeanProperty
import scala.reflect.api.materializeTypeTag
import it.almawave.linkeddata.kb.utils.ModelAdapter

object TestingMapsModel extends App {

  val map = Map(
    "uri" -> new URI("http://uno/"),
    "extra" -> "some other non-mapped data!",
    "description" -> "this is the description of the element",
    "hierarchy" -> List("http://uno/", "http://uno/01", "http://uno/01/A"))

  val obj = ModelAdapter.fromMap[Item](map)
  println(obj)

  //  val result = ModelAdapter.toMap(Item(new URI("http://due"), List(), None)).asJava
  //  println(result)
  //
  //  val email = Map("username" -> "seralf", "password" -> "secret")
  //
  //  println(ModelAdapter.fromMap[EmailAccount](email))

}

class EmailAccount {

  @BeanProperty var username: String = ""
  @BeanProperty var password: String = ""

  override def toString() = s"""auth:${username}:${password}"""
}

// EXAMPLES

case class Item(uri: URI, hierarchy: Seq[String], description: Option[String], comment: Option[String])

case class RDF_URI(uri: URI) {

  val id = uri.toString()
    .replaceAll("^(.*)[#/]$", "$1")
    .replaceAll(".*[#/](.*)", "$1")

  override def toString() = s"""URI[${id}]"""

}

case class TestElement01(t: String, ot: Option[String])
case class TestElement02(hello: String, answer: Int)


//  fromMap[TestElement01](Map("t" -> "test", "ot" -> "test2")) == Test("test", Some("test2"))
//  fromMap[TestElement01](Map("t" -> "test")) == Test("test", None)
//  val obj = fromMap[TestElement01](Map("t" -> "test", "ot" -> "test2"))
//  val obj = fromMap[TestElement02](Map("hello" -> "world", "answer" -> 42))