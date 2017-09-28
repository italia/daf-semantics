package it.almawave.linkeddata.kb.utils

import java.io.OutputStream
import scala.util.Try
import it.almawave.linkeddata.kb.utils.TryHandlers.TryLog

object CSVHelper {
  def create() = new CSVHelper()
}

// TODO: import of CSVHelper library, or externalize parameters here
class CSVHelper(
    val CELL_DELIMITER: String = ";",
    val TEXT_DELIMITER: String = "\"") {

  private def seqToRow(list: Seq[Object]) = {
    list.map { item =>
      item match {
        case txt: String => s"""${TEXT_DELIMITER}${txt}${TEXT_DELIMITER}"""
        case obj         => obj
      }
    }
  }

  private def mapToRow(map: Map[String, Object]) =
    seqToRow(map.map { _._2 }.toSeq)

  private def mapToHeader(map: Map[String, Object]) =
    map.map { _._1.toString() }

  /**
   * writing CSV to the provided OutputStream
   */
  def writeToOutputStream(dataset: List[List[Map[String, Object]]], out: OutputStream) = {

    if (!dataset.isEmpty) {

//      val header = mapToHeader(dataset.head.)
//      println("HEADER: " + header)
//      
//
//      out.write(header.mkString(CELL_DELIMITER).getBytes)
//      out.write('\n')
//
//      dataset.tail.foreach { line =>
//        val row = mapToRow(line)
//        out.write(row.mkString(CELL_DELIMITER).getBytes)
//        out.write('\n')
//      }

    }

  }

}
