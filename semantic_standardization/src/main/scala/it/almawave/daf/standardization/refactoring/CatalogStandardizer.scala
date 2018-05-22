package it.almawave.daf.standardization.refactoring

import it.almawave.linkeddata.kb.catalog.CatalogBox
import it.almawave.linkeddata.kb.catalog.VocabularyBox
import it.almawave.linkeddata.kb.catalog.SPARQL
import scala.util.Try
import scala.Stream
import scala.collection.mutable.ListBuffer
import it.almawave.linkeddata.kb.utils.ModelAdapter
import java.io.OutputStream
import java.io.ByteArrayOutputStream
import org.slf4j.LoggerFactory

object CatalogStandardizer {

  def apply(catalog: CatalogBox) = new CatalogStandardizer(catalog)

}

/**
 * this class provides support for DAF-standardization
 *
 */
class CatalogStandardizer(catalog: CatalogBox) {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val BASE: Seq[String] = List("https://w3id.org/italia/onto/", "http://dati.gov.it/onto/")

  def start = catalog.start()
  def stop = catalog.stop()

  def getVocabularyStandardizersList() = {
    catalog.vocabulariesWithDependencies()
      .zipWithIndex
      .map {
        case (vbox, i) =>

          vbox.start()

          val vbox_all = catalog.resolveVocabularyDependencies(vbox)
          //          vbox_all.start()

          val index = "%02d".format(i)
          logger.debug(s"preparing vocabulary[$index] ${vbox} for standardization.")
          //    REVIEW HERE federated deps
          //          logger.debug(s"preparing vocabulary[$index] ${vbox_all} for standardization.")

          new VocabularyStandardizer(vbox_all)
      }
  }

  def getVocabularyStandardizerByID(vocID: String) = Try {
    getVocabularyStandardizersList.filter(_.vbox.id == vocID).head
  }

}

