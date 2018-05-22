package it.almawave.daf.standardization.refactoring

import org.slf4j.LoggerFactory
import java.nio.file.Paths
import com.typesafe.config.ConfigFactory
import it.almawave.linkeddata.kb.catalog.CatalogBox
import scala.util.Try

object NO_MainAllStandardization extends App {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val conf = ConfigFactory.parseFile(Paths.get("./conf/catalog.conf").normalize().toFile())

  val catalog = new CatalogBox(conf)
  catalog.start()

  CatalogStandardizer(catalog).getVocabularyStandardizersList()
    .zipWithIndex
    .slice(1, 2)
    .toList
    .foreach {
      case (std, i) =>

        Try {
          println(s"""\n\n$i: ${std.vbox}""")
          println("\n\nCSV_______________________________________")
          std.toCSV()(System.out)
          println("\n\n__________________________________________")
        }

    }

  catalog.stop()

}