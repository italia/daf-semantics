package it.almawave.kb

import java.nio.file.Paths
import org.eclipse.rdf4j.rio.Rio

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import it.almawave.kb.repo.RDFRepository

object MainOntoMeta extends App {

  //  val repo = RDFRepository.memory()
  val repo = RDFRepository.virtuoso()

  println("starting....")

  repo.start()

  repo.helper.importFrom("dist/data/ontologies")

  val prefixes = repo.prefixes.list()
  println("#### PREFIXES\n" + prefixes.mkString("\n"))

  repo.stop()

  //  System.exit(0) ----

  val uri = Paths.get("dist/data/ontologies/agid/CPSV-AP_IT/CPSV-AP_IT.owl").normalize().toUri()
  val format = Rio.getParserFormatForFileName(uri.toString()).get
  val doc = Rio.parse(uri.toURL().openStream(), uri.toString(), format)

  val namespaces = doc.getNamespaces

  namespaces.foreach { ns =>
    println(ns)
  }

  val base = doc.getNamespace("").get.getName.trim()
  val base_prefix = base.replaceAll("^(.*)/(.*)[/#]$", "$2").toLowerCase()
  println("DEFAULT: " + base, base_prefix)

  // example: xml:base="http://dati.gov.it/onto/cpsv-itap/"

}