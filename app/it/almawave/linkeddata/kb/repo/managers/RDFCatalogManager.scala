package it.almawave.linkeddata.kb.repo.managers

import org.slf4j.LoggerFactory
import com.typesafe.config.ConfigFactory
import org.eclipse.rdf4j.rio.RDFFormat
import java.io.File
import java.net.URLDecoder
import org.eclipse.rdf4j.rio.Rio
import java.io.FileInputStream
import java.nio.file.Paths

import it.almawave.linkeddata.kb.utils.TryHandlers._
import it.almawave.linkeddata.kb.utils.RDF4JAdapters._
import java.nio.file.Files
import java.net.URI
import com.typesafe.config.Config
import java.nio.file.StandardCopyOption
import java.nio.file.Path
import it.almawave.linkeddata.kb.repo.RDFRepositoryBase
import scala.util.Try
import java.io.InputStream
import java.net.URL
import org.eclipse.rdf4j.model.Model
import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.model.util.Models
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import scala.collection.mutable.ListBuffer
import org.eclipse.rdf4j.query.QueryLanguage
import org.eclipse.rdf4j.model.Namespace

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
 * basic abstraction for handling ontologies / vocabularies
 *
 * TODO: add metadata
 * TODO: add case classes as models
 *
 */
class RDFCatalogManager(kbrepo: RDFRepositoryBase) {

  implicit val logger = LoggerFactory.getLogger(this.getClass)

  /*
   * TODO: we need a persistance to handle the lists, 
   * or do we need to retrieve them from triplestore?
   * 
   * IDEA:
   * 	1) explore contexts
   * 	2) filter by rootURI
   * 	3.a) ?uri a owl:Ontology -> ?uri is ontology!
   *	3.b) ?uri a skos:ConceptScheme -> ?uri is a vocabulary!
   *   
   */
  def ontologies(): Seq[String] = ???
  def vocabularies(): Seq[String] = ???

  /*
   *  CHECK: contexts
   *  1) ontology baseURI as context
   *  2) prefix from ontologyID
   *  
   *  TODO: move to OntologyMeta / Ontology case classes, with metadata
   */
  def addOntology(
    rdfURL: URL, mime: String,
    ontologyID: String,
    prefix: String, namespace: String,
    contexts: String*) = Try {

    // detect format
    val format: RDFFormat = chooseMimeFormat(rdfURL, mime).get

    // assign default baseURI
    val baseURI = if (namespace != null) namespace else ""

    // parse RDF model
    val doc = Rio.parse(rdfURL.openStream(), baseURI, format)

    // getting namespaces
    val namespaces: Map[String, String] = doc.getNamespaces
      .map { ns => (ns.getPrefix, ns.getName) }
      .toMap + (prefix -> namespace)

    // adding document to provided contexts
    kbrepo.store.add(doc, contexts: _*)
    // adding document to default context
    kbrepo.store.add(doc)
    // adding extracted prefixes
    kbrepo.prefixes.add(namespaces.toList: _*)

    // TODO: rule for ontologyID / prefix
    // TODO: dependencies
    // TODO: alignments
    // TODO: file/db storage for doc metadata
    // TODO: RDFOntology case class

  }

  def removeOntologyByURI(baseURI: String) = {
    kbrepo.store.clear(baseURI)
  }

  def removeOntologyByID() = ???

  def addVocabulary(
    rdfURL: URL, mime: String,
    vocabularyID: String,
    namespace: String,
    contexts: String*) = Try {

    // detect format
    val format = chooseMimeFormat(rdfURL, mime).get

    // assign default baseURI
    val baseURI = if (namespace != null) namespace else ""

    // parse RDF model
    val doc = Rio.parse(rdfURL.openStream(), baseURI, format)

    // getting namespaces
    val namespaces = doc.getNamespaces.map { ns => (ns.getPrefix, ns.getName) }.toMap

    // adding document to provided contexts
    kbrepo.store.add(doc, contexts: _*).get
    // adding document to default context
    kbrepo.store.add(doc).get
    // adding extracted prefixes
    kbrepo.prefixes.add(namespaces.toList: _*)

    // TODO: rule for ontologyID / prefix
    // TODO: dependencies
    // TODO: alignments
    // TODO: file/db storage for doc metadata
    // TODO: RDFOntology case class

  }

  def removeVocabularyByURI(baseURI: String) = {
    kbrepo.store.clear(baseURI)
  }

  def removeVocabularyByID() = ???

  private def chooseMimeFormat(rdfURL: URL, mime: String = "text/turtle") = {
    if (mime != null)
      Rio.getParserFormatForMIMEType(mime)
    else
      Rio.getParserFormatForFileName(rdfURL.getPath.toString())
  }

}

// TODO
case class OntologyMeta(
    url: URL, mime: String,
    ontologyID: String, description: String,
    prefix: String, namespace: String,
    contexts: Seq[String]) {

  val dependencies: Seq[String] = Seq() // TODO
  val links: Seq[String] = Seq() // TODO

}

// TODO
case class RDFOntologyDocument(meta: OntologyMeta) {

  val _vf = SimpleValueFactory.getInstance
  private val _contexts = meta.contexts.map { cx => _vf.createIRI(cx) }

  val _baseURI = meta.namespace
  val _format: RDFFormat = Rio.getParserFormatForMIMEType(meta.mime).get
  val _model = Rio.parse(meta.url.openStream(), _baseURI, _format, _contexts: _*)

  private lazy val ns_list = _model.getNamespaces.map { ns => (ns.getPrefix, ns.getName) }

  val contexts = _model.contexts().map { cx => cx.stringValue() }.toSeq

  // if contexts were provided, the baseURI is by convention the first one (no prefix)
  val baseURI: Option[String] = ns_list
    .filter(_._1.trim().equals("")) match {
      case empty if (empty.isEmpty) => None
      case items                    => Some(items.head._2)
    }

  val namespaces: Map[String, String] = ns_list
    .map { ns =>
      if (ns._1.equals("")) {
        val px = ns._2
          .replaceAll("^.*/(.*)[#/]$", "$1")
          .toLowerCase()
        (px, ns._2)
      } else {
        ns
      }
    }
    .toMap

  def concepts = sparql
    .query("""SELECT DISTINCT ?concept WHERE { [] a ?concept }""")
    .flatMap { _.map { _._2.toString() } }

  def model = _model

  object sparql {

    // TODO: refactorization
    def query(query: String) = {

      val repo = new SailRepository(new MemoryStore)
      repo.initialize()
      val conn = repo.getConnection
      conn.add(_model)
      val results = new ListBuffer[Map[String, Object]]
      val tuples = conn.prepareTupleQuery(QueryLanguage.SPARQL, query, baseURI.getOrElse(null)).evaluate()
      while (tuples.hasNext()) {
        results += tuples.next().map { bs => (bs.getName, bs.getValue) }.toMap
      }
      conn.close()
      repo.shutDown()

      results.toStream
    }

  }

  private val BNODES_ON = true
  def bnodes = {

    if (BNODES_ON)
      Models.subjectBNodes(_model).toStream
    else
      Models.subjectBNodes(_model).map { bn =>
        val id = bn.getID.replaceAll("_:", "")
        s"${baseURI}.well-known/bnode/${id}" // template for well-known bnodes
      }.toStream

  }

  override def toString = {

    s"""
      RDFDocument
      
      prefix: ${meta.prefix}
      baseURI: ${baseURI.getOrElse("")}
      
      namespaces: 
        ${namespaces.mkString("\n\t")}
      
      n° of bnodes: ${bnodes.distinct.size}
      
      concepts:
        ${concepts.mkString("\n\t")}
      
    """.trim()

  }

}

class OLDRDFDocumentParser(model: Model) {

  // prefix is used for ontologies
  private var _prefix: Option[String] = None

  def prefix = _prefix

  val BNODES_ON = false

  val vf = SimpleValueFactory.getInstance

  val baseURI = model.getNamespaces
    .map { ns => (ns.getPrefix, ns.getName) }
    .filter(_._1.trim().equals("")) match {
      case empty if (empty.isEmpty) => None
      case items                    => Some(items.head._2)
    }

  val namespaces: Map[String, String] = model.getNamespaces
    .map { ns => (ns.getPrefix, ns.getName) }
    .map { ns =>
      if (ns._1.equals("")) {
        _prefix = Some(ns._2
          .replaceAll("^.*/(.*)[#/]$", "$1")
          .toLowerCase())
        (prefix.get, ns._2)
      } else {
        _prefix = None
        ns
      }
    }
    .toMap

  def contexts = model.contexts().toList

  def subjects = model.subjects().map(_.toString()).toList

  def concepts = sparql
    .query("""SELECT DISTINCT ?concept WHERE { [] a ?concept }""")
    .flatMap { _.map { _._2.toString() } }

  object sparql {

    // TODO: refactorization
    def query(query: String) = {

      val repo = new SailRepository(new MemoryStore)
      repo.initialize()
      val conn = repo.getConnection
      conn.add(model)
      val results = new ListBuffer[Map[String, Object]]
      val tuples = conn.prepareTupleQuery(QueryLanguage.SPARQL, query, baseURI.getOrElse(null)).evaluate()
      while (tuples.hasNext()) {
        results += tuples.next().map { bs => (bs.getName, bs.getValue) }.toMap
      }
      conn.close()
      repo.shutDown()

      results.toStream
    }

  }

  // TODO: find a way to avoid duplicates
  def bnodes = {

    if (BNODES_ON)
      Models.subjectBNodes(model)
        .toStream
    else
      Models.subjectBNodes(model).map { bn =>
        println("ok")
        val id = bn.getID.replaceAll("_:", "")
        s"${baseURI}.well-known/bnode/${id}" // template for well-known bnodes
      }.toStream

  }

  override def toString = {

    s"""
      RDFDocument
      
      prefix: ${prefix}
      baseURI: ${baseURI.getOrElse("")}
      
      namespaces: 
        ${namespaces.mkString("\n\t")}
      
      n° of subjects: ${subjects.distinct.size}
      n° of bnodes: ${bnodes.distinct.size}
      
      concepts:
        ${concepts.mkString("\n\t")}
      
    """.trim()

  }

}

