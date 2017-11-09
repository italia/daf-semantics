package it.gov.daf.semantics.api

import com.typesafe.config.Config
import com.typesafe.config.ConfigValueFactory
import com.typesafe.config.ConfigFactory
import org.eclipse.rdf4j.rio.Rio

import it.almawave.linkeddata.kb.repo.RDFRepository

import java.nio.file.Files
import java.nio.file.Paths
import java.io.File

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

object AnnotationLookupAPI {

  val DEFAULT_CONFIG = ConfigFactory.parseString("""
    
  # lookup configuration for an ontology and its related vocabularies
  POI-AP_IT {
    
    files: [ 
      ${data_dir}"/ontologies/agid/POI-AP_IT/POI-AP_IT.ttl",
      ${data_dir}"/ontologies/l0.ttl",
      ${data_dir}"/vocabularies/POICategoryClassification.ttl"
    ]
    
    contexts: [ "http://dati.gov.it/onto/poiapit#" ]
        
    lookup.annotation: ${data_dir}"/references/lookup_semantic_annotation.sparql"

  }
  
  # REVIEW: lookup query can't work with class or instances in this way...
  CLV_AP-IT {
  
    files: [ 
      ${data_dir}"/ontologies/agid/CLV-AP_IT/CLV-AP_IT.ttl",
      ${data_dir}"/ontologies/l0.ttl",
      ${data_dir}"/vocabularies/Istat-Classificazione-08-Territorio.ttl"
    ]
    
    contexts: [ "http://dati.gov.it/onto/clvapit#" ]
    
    # TODO: REVIEW for CLV
    lookup.annotation: ${data_dir}"/references/lookup_semantic_annotation.sparql"
  
  }
  
  """)

}

class AnnotationLookupAPI() {

  val repo = RDFRepository.memory()

  //  val conf = config.withValue("data_dir", ConfigValueFactory.fromAnyRef("dist/data")).resolve()

  var conf = ConfigFactory.empty()

  def config(config: Config) {
    conf = config
  }

  def start = {
    repo.start()
    this.load()
  }

  def stop = repo.stop()

  def mime(file: File) = Rio.getParserFormatForFileName(file.getName).get.getDefaultMIMEType

  /*
   * loads ontologies and vocabularies for each configuration 
   */
  def load() = {

    conf.root().keySet().map { key =>

      val files_path = key.trim() + ".files"
      val contexts_path = key.trim() + ".contexts"

      if (conf.hasPath(contexts_path)) {

        val contexts = conf.getStringList(contexts_path)

        val files = conf.getStringList(files_path).map { f => new File(f) }
        files.foreach { file => repo.io.addRDFFile(file, mime(file), contexts: _*) }

      }

    }

  }

  def lookup(semantic_annotation: String) = {

    val annotations = semantic_annotation.split("\\.")
    val ontology_id = annotations(0)
    val concept_id = annotations(1)
    val property_id = annotations(2)

    val query = Files.readAllLines(Paths.get(conf.getString(s"${ontology_id}.lookup.annotation")))
      .mkString("\n")
      .replace("${semantic_annotation}", semantic_annotation)

    repo.sparql.query(query)

  }

}

