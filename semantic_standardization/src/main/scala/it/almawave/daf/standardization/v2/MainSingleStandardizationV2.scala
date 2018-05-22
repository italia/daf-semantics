package it.almawave.daf.standardization.v2

import org.slf4j.LoggerFactory
import java.nio.file.Paths
import it.almawave.linkeddata.kb.catalog.CatalogBox
import com.typesafe.config.ConfigFactory
import scala.util.Try
import it.almawave.linkeddata.kb.utils.JSONHelper
import it.almawave.daf.standardization.v1.StandardizationProcessV1

@Deprecated
object MainSingleStandardizationV2 extends App {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val conf = ConfigFactory.parseFile(Paths.get("./conf/catalog.conf").normalize().toFile())

  val catalog = new CatalogBox(conf)
  catalog.start()

  val std = new StandardizationProcessV1(catalog)

  // DEBUG single
  val vocID = "licences"
  val vbox = std.vocabularyWithDependency(vocID).get

  vbox.start()

  val _try = Try {

    logger.info(s"\n\n#### Vocabulary: ${vbox} ####")

    val cells = std.itemsByVocabularyBox_v2(vbox, "it")

    val json = JSONHelper.writeToString(cells)
    println(json)

  }

  // check errors
  if (_try.isFailure) {
    val err = _try.failed.get
    System.err.println("ERROR FOR " + vbox)
    System.err.println("ERR: " + err)
    err.printStackTrace(System.err)
  }

  vbox.stop()

  catalog.stop()

}