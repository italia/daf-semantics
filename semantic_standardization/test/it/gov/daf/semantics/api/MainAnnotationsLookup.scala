package it.gov.daf.semantics.api

import it.almawave.linkeddata.kb.repo.RDFRepository
import scala.io.Source
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config
import com.typesafe.config.ConfigValueFactory
import org.eclipse.rdf4j.rio.Rio
import it.almawave.linkeddata.kb.repo.utils.ConfigHelper

import java.nio.file.Paths
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

object MainAnnotationsLookup extends App {

  val config_annotations = ConfigHelper.injectParameter(AnnotationLookupAPI.DEFAULT_CONFIG, "data_dir", "./dist/data")

  val api = new AnnotationLookupAPI()
  api.config(config_annotations)
  api.start

//  val semantic_annotation = "POI-AP_IT.PointOfInterestCategory.POIcategoryIdentifier"
  val semantic_annotation = "CLV_AP-IT.Region.name"

  println(s"given input: ${semantic_annotation}\nreturns:")
  api.lookup(semantic_annotation)
    .get
    .foreach { item =>
      val row = item.mkString("\n")
      println(row)
    }

  api.stop
}
