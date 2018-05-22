package it.almawave.kb.sparqlnew

object Framing {

  def mergeTuples(tuples: Seq[Map[String, Any]]) = {

    // IDEA for merging multiple tuples/maps into one! (with lists of values instead of single values)
    val buffer = scala.collection.mutable.Map[String, Any]()
    for {

      tuple <- tuples
      element <- tuple

    } yield {

      val key = element._1
      val value_original = element._2

      if (!buffer.contains(key)) {
        //        println(s"KEY ${key} NOT FOUND! ADDING ${value_original}")
        buffer += element
      } else {

        val value_modified = buffer.get(key).get match {
          case list: List[_] =>
            //            println(s"KEY ${key} FOUND! UPDATING ${list.mkString("|")} + ${value_original}")
            list ++ List(value_original)
          case single: Any =>
            //            println(s"KEY ${key} FOUND! UPDATING LIST(${value_original})")
            List(single, value_original)
          case _ => null // WEIRD?
        }

        buffer += ((key, value_modified))
      }

    }

    buffer.toList.toMap
  }

}
