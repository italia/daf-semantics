package it.almawave.daf.standardization.refactoring

import it.almawave.kb.http.models.Hierarchy
import scala.collection.mutable.ListBuffer

// TODO: rewrite as JUnit
object CheckReducepath extends App {

  val e_A = StdHierarchy("http://elements/A", List("http://elements/A"))
  val e_A1 = StdHierarchy("http://elements/A1", List("http://elements/A", "http://elements/A1"))
  val e_A11 = StdHierarchy("http://elements/A11", List("http://elements/A", "http://elements/A1", "http://elements/A11"))
  val e_D = StdHierarchy("http://elements/D", List("http://elements/D"))

  val elements = List(e_A, e_A1, e_A11, e_D)

  val results = elements.foldLeft(new ListBuffer[StdHierarchy]) { (list, h) =>

    val test = list.forall { el =>
      h.path.mkString("|").contains(el.path.mkString("|"))
    }

    println(s"${h} :: ${list.mkString("|")} :: ${test}")

    //    if (!test)
    //      list
    //    else
    list += h

  }

  println("\n\nRESULTS")
  results.foreach(println)

}