//package it.almawave.linkeddata.kb.repo.parsers
//
//import org.eclipse.rdf4j.model.impl.SimpleValueFactory
//import org.eclipse.rdf4j.rio.Rio
//import org.eclipse.rdf4j.model.Model
//
//import scala.collection.JavaConversions._
//import scala.collection.JavaConverters._
//import java.net.URL
//import org.eclipse.rdf4j.repository.sail.SailRepository
//import org.eclipse.rdf4j.sail.memory.MemoryStore
//import scala.collection.mutable.ListBuffer
//import org.eclipse.rdf4j.query.QueryLanguage
//import org.eclipse.rdf4j.model.util.Models
//
//object RDFParser {
//
//  def parseOntology(url: URL, baseURI: String = null): RDFDocument = {
//
//    val vf = SimpleValueFactory.getInstance
//
//    val format = Rio.getParserFormatForFileName(url.getPath).get
//    val mime = format.getDefaultMIMEType
//    val ext = format.getDefaultFileExtension
//    val base = if (baseURI != null) baseURI else ""
//
//    val doc = Rio.parse(url.openStream(), base, format)
//
//    RDFDocument(doc.unmodifiable())
//
//  }
//
//}
