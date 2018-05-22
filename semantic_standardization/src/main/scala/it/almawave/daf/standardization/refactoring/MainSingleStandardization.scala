package it.almawave.daf.standardization.refactoring

import org.slf4j.LoggerFactory
import java.nio.file.Paths
import it.almawave.linkeddata.kb.catalog.CatalogBox
import com.typesafe.config.ConfigFactory
import it.almawave.linkeddata.kb.utils.JSONHelper

import it.almawave.linkeddata.kb.catalog.VocabularyBox

object MainSingleStandardization extends App {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val conf = ConfigFactory.parseFile(Paths.get("./conf/catalog.conf").normalize().toFile())

  val catalog = new CatalogBox(conf)
  catalog.start()

  //  val vocID = "legal-status"
  //  val vocID = "theme-subtheme-mapping"
  val vocID = "licences"
  val std: VocabularyStandardizer = CatalogStandardizer(catalog).getVocabularyStandardizerByID(vocID).get
  std.start

  //  println("\n\nCSV")
  //  std.toCSV()(System.out)
  //
  //  println("\n\nTREE")
  val tree = std.toJSONTree()
  val json_tree = JSONHelper.writeToString(tree)
  println(json_tree)

  println("\n\nMETA")
  val meta = std.getMetadata()
  val json_meta = JSONHelper.writeToString(meta)
  println(json_meta)

  std.stop
  catalog.stop()

  // TODO: verify the closing of all active connections

}

object MainStandardizationAll extends App {

  private val logger = LoggerFactory.getLogger(this.getClass)
  val conf = ConfigFactory.parseFile(Paths.get("./conf/catalog.conf").normalize().toFile())

  val catalog = new CatalogBox(conf)
  catalog.start()

  val std = CatalogStandardizer(catalog)
  std.start

  val list = std.getVocabularyStandardizersList()

  list.foreach { vstd =>
    //    println(s"\n\nCSV for ${vstd.vbox}")
    vstd.toCSV()(System.out)
  }

  std.stop
  catalog.stop()

  System.exit(0)
}
