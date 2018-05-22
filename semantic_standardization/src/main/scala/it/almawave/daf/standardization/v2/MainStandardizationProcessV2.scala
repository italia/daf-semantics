package it.almawave.daf.standardization.v2

import scala.util.Try
import org.slf4j.LoggerFactory
import com.typesafe.config.ConfigFactory
import java.nio.file.Paths
import it.almawave.linkeddata.kb.catalog.CatalogBox
import it.almawave.linkeddata.kb.utils.JSONHelper
import it.almawave.daf.standardization.v1.StandardizationProcessV1

/*
 * TODO: write Unit test for each task
 *
 * TODO: hiearchy for single element!
 *
 */
object MainStandardizationProcessV2 extends App {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val conf = ConfigFactory.parseFile(Paths.get("./conf/catalog.conf").normalize().toFile())
  val catalog = new CatalogBox(conf)
  catalog.start()

  val std = new StandardizationProcessV1(catalog)

  // ALL
  std.vocabulariesWithDependencies().foreach { vbox =>

    vbox.start()

    val _try = Try {

      logger.info(s"\n\n#### Vocabulary: ${vbox} ####")
      logger.info(s"\n\n#### TYPE: ${vbox.extract_assetType()} ####")

      // DEBUG

      val cells = std.itemsByVocabularyBox_v2(vbox) // V2

      logger.debug(JSONHelper.writeToString(cells.toList))

      val MAX_LEVELS = std.max_levels(vbox).get
      logger.info("MAX_LEVELS: " + MAX_LEVELS)

      logger.info(s"#############################\n\n")
    }

    if (_try.isFailure) {
      val err = _try.failed.get
      System.err.println("ERROR FOR " + vbox)
      System.err.println("ERR: " + err)
      err.printStackTrace(System.err)
    }

    // SEE: Istat-Classificazione-08-Territorio

    vbox.stop()

  } // ALL

  catalog.stop()
}