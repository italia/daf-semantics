package it.almawave.kb.repo

import java.net.URI
import java.nio.file.Paths

import org.eclipse.rdf4j.model.IRI
import org.eclipse.rdf4j.model.Model
import org.eclipse.rdf4j.model.Resource
import org.eclipse.rdf4j.model.Value
import org.eclipse.rdf4j.model.ValueFactory
import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.model.vocabulary._
import org.eclipse.rdf4j.query.QueryLanguage
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository
import org.eclipse.rdf4j.rio.Rio
import org.eclipse.rdf4j.sail.memory.MemoryStore

import it.almawave.kb.FileDatastore
import it.almawave.kb.RDFHelper.RepositoryResultIterator
import it.almawave.kb.RDFHelper.TupleResultIterator
import play.Logger

import it.almawave.kb.ConfigHelper
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.model.Statement

import java.net.URL
import java.net.URLDecoder
import java.io.FileInputStream
import java.io.File
import org.eclipse.rdf4j.model.Literal
import org.eclipse.rdf4j.repository.RepositoryConnection
import virtuoso.rdf4j.driver.VirtuosoRepository

import org.eclipse.rdf4j.sail.memory.model._
import scala.collection.JavaConversions._

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import org.eclipse.rdf4j.model.impl.SimpleIRI
import org.eclipse.rdf4j.model.impl.NumericLiteral

object RDFRepository {

  def remote(endpoint: String) = {
    new RDFRepositoryBase(new SPARQLRepository(endpoint, endpoint))
  }

  def memory() = {

    // CHECK: how to handle contexts properly

    val mem = new MemoryStore
    val repo: Repository = new SailRepository(mem)
    new RDFRepositoryBase(repo)
  }

  // TODO: config
  def memory(dir_cache: String) = {

    val dataDir = Paths.get(dir_cache).normalize().toAbsolutePath().toFile()
    if (!dataDir.exists())
      dataDir.mkdirs()
    val mem = new MemoryStore()
    mem.setDataDir(dataDir)
    mem.setSyncDelay(1000L)
    mem.setPersist(false)
    mem.setConnectionTimeOut(1000) // TODO: set a good timeout!

    // IDEA: see how to trace statements added by inferencing
    // CHECK val inferencer = new DedupingInferencer(new ForwardChainingRDFSInferencer(new DirectTypeHierarchyInferencer(mem)))
    // SEE CustomGraphQueryInferencer

    val repo = new SailRepository(mem)
    new RDFRepositoryBase(repo)
  }

  /* 
  TODO: virtuoso
   */
  def virtuoso() = {
    // TODO: externalize configurations
    // TODO: add a factory for switching between dev / prod
    val host = "localhost"
    val port = 1111
    val username = "dba"
    val password = "dba"
    //    val repo = new VirtuosoRepository(s"jdbc:virtuoso://${host}:${port}/charset=UTF-8/log_enable=2", username, password)

    val repo = new VirtuosoRepository(s"jdbc:virtuoso://localhost:1111/charset=UTF-8/log_enable=2", "dba", "dba")

    new RDFRepositoryBase(repo)
  }

  /* DISABLED */
  def solr() {

    //    val index = new SolrIndex()
    //    val sailProperties = new Properties()
    //    sailProperties.put(SolrIndex.SERVER_KEY, "embedded:")
    //    index.initialize(sailProperties)
    //    val client = index.getClient()
    //
    //    val memoryStore = new MemoryStore()
    //    // enable lock tracking
    //    org.eclipse.rdf4j.common.concurrent.locks.Properties.setLockTrackingEnabled(true)
    //    val lucenesail = new LuceneSail()
    //    lucenesail.setBaseSail(memoryStore)
    //    lucenesail.setLuceneIndex(index)
    //
    //    val repo = new SailRepository(lucenesail)

  }

}

// TODO: refactorization using trait!!
trait RDFRepository

/**
 *
 * TODO: use an implicit connection
 * TODO: provide a connection pool
 * TODO: add an update method (remove + add) using the same connection/transaction
 *
 */
class RDFRepositoryBase(repo: Repository) {

  //  val logger = LoggerFactory.getLogger(this.getClass)

  val logger = Logger.underlying()

  val _self = this

  // CHECK: providing custom implementation for BN
  var vf: ValueFactory = SimpleValueFactory.getInstance

  // checking if the repository is up. TODO: refactoring to a method
  def check(): Boolean = {
    val result = try {
      val _conn = repo.getConnection
      _conn.close()
      true
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        logger.error(s"error attempting connection to repository") // TODO: config with url
        false
    }
    println("???? CHECKING ???? " + result)
    result 
  }

  def start() {

    if (!repo.isInitialized())
      repo.initialize()

    this.check()

    vf = repo.getValueFactory
  }

  def stop() {
    if (repo.isInitialized())
      repo.shutDown()
  }

  object prefixes {

    def clear() = {
      val conn = repo.getConnection
      conn.begin()
      try {
        conn.clearNamespaces()
        conn.commit()
      } catch {
        case ex: Exception =>
          logger.error(s"error while removing namespaces!")
          conn.rollback()
      }
      conn.close()
    }

    def add(namespaces: (String, String)*) {
      val conn = repo.getConnection
      conn.begin()
      try {
        namespaces.foreach { pair => conn.setNamespace(pair._1, pair._2) }
        conn.commit()
      } catch {
        case ex: Exception =>
          logger.error(s"KB:RDF> cannot add namespaces: ${namespaces}")
          conn.rollback()
      }
      conn.close()
    }

    def remove(namespaces: (String, String)*) {
      val conn = repo.getConnection
      conn.begin()
      try {
        namespaces.foreach { pair => conn.setNamespace(pair._1, pair._2) }
        conn.commit()
      } catch {
        case ex: Exception =>
          logger.error(s"KB:RDF> cannot remove namespaces: ${namespaces}")
          conn.rollback()
      }
      conn.close()
    }

    // get prefixes
    def list(): Map[String, String] = {

      val conn = repo.getConnection

      val namespaces = conn.getNamespaces.toList
        .map { ns => (ns.getPrefix, ns.getName) }
        .toMap

      conn.close()

      namespaces

    }

    val DEFAULT = Map(
      OWL.PREFIX -> OWL.NAMESPACE,
      RDF.PREFIX -> RDF.NAMESPACE,
      RDFS.PREFIX -> RDFS.NAMESPACE,
      DC.PREFIX -> DC.NAMESPACE,
      FOAF.PREFIX -> FOAF.NAMESPACE,
      SKOS.PREFIX -> SKOS.NAMESPACE,
      XMLSchema.PREFIX -> XMLSchema.NAMESPACE,
      FN.PREFIX -> FN.NAMESPACE,
      "doap" -> DOAP.NAME.toString(),
      "geo" -> GEO.NAMESPACE,
      SD.PREFIX -> SD.NAMESPACE)

  }

  def connection = repo.getConnection

  /*
   * this component can be seen as an RDF datastore abstraction
   */
  object store {

    def clear(contexts: Resource*) {
      val conn = repo.getConnection
      conn.begin()

      try {
        if (contexts.size > 0) {
          conn.clear(contexts: _*)
        } else {
          // delete each contexts
          conn.clear(null)
          conn.clear()
          val _contexts = conn.getContextIDs.toList
          conn.clear(_contexts: _*)
        }
        conn.commit()
      } catch {
        case ex: Exception =>
          logger.error(s"KB:RDF> cannot clear contexts: ${contexts.mkString(", ")}")
          conn.rollback()
      }

      conn.close()
    }

    //    @Deprecated
    //    def __clear(contexts: Resource*) {
    //
    //      val conn = repo.getConnection
    //      conn.begin()
    //
    //      println(s"\n\nCONTEXTS\n [ ${contexts.mkString(" | ")} ]")
    //
    //      try {
    //
    //        conn.clear(contexts: _*)
    //
    //        conn.commit()
    //
    //      } catch {
    //        case ex: Exception =>
    //          logger.error(s"KB:RDF> cannot clear contexts: ${contexts.mkString(", ")}")
    //          conn.rollback()
    //      }
    //
    //      conn.close()
    //
    //    }

    def contexts(): Seq[String] = {

      val conn = repo.getConnection

      val results: Seq[String] = conn.getContextIDs.map { ctx => ctx.stringValue() }.toList
      conn.close()

      results
    }

    // TODO: refactorize / merge the two signatures!
    //    def sizeByContexts(contexts: Seq[String]): Long = {
    //      val ctxs = contexts.map { cx => vf.createIRI(cx) }.toList
    //      this.size(ctxs: _*)
    //    }

    def size(contexts: Resource*): Long = {
      val conn = repo.getConnection

      val size = if (contexts.size > 0)
        conn.size(contexts: _*)
      else {
        conn.size(null)
        //        conn.clear()
      }

      //      var size = conn.size(contexts: _*) // CHECK: blank nodes!

      conn.close()
      size
    }

    def statements(s: Resource, p: IRI, o: Value, inferred: Boolean, contexts: Resource*) = {
      val conn = repo.getConnection
      // CHECK: not efficient!
      val results = conn.getStatements(null, null, null, false, contexts: _*).toList
      conn.close()

      results.toStream
    }

    def add(doc: Model, contexts: Resource*) {

      // merge the contexts
      // REVIEW HERE: val ctxs = doc.contexts().toSeq.union(contexts.toSeq).distinct

      val ctxs = contexts

      val conn = repo.getConnection()
      conn.begin()

      try {
        conn.add(doc, ctxs: _*)
        conn.commit()
        logger.debug(s"KB:RDF> ${doc.size()} triples was added to contexts ${ctxs.mkString(" | ")}")
      } catch {
        case ex: Exception =>
          logger.debug(s"KB:RDF> cannot add RDF data\n${ex} in ${contexts.mkString("|")}")
          conn.rollback()
      }

      conn.close()
    }

    def remove(doc: Model, contexts: Resource*) {

      // merge the contexts
      //      val ctxs = doc.contexts().toSeq.union(contexts.toSeq).distinct

      val ctxs = contexts

      val conn = repo.getConnection()
      conn.begin()

      try {
        conn.remove(doc, ctxs: _*)
        conn.commit()
        logger.debug(s"KB:RDF> ${doc.size()} triples was removed from contexts ${ctxs.mkString(" | ")}")
      } catch {
        case ex: Exception =>
          // CHECK: we could try to remove from every single context
          logger.debug(s"KB:RDF> cannot remove RDF data\n${ex}")
          conn.rollback()
      }

      conn.close()
    }

  }

  /*
   * this part can be seen as a sparql datastore abstraction
   */
  object sparql {

    def query(query: String): Seq[Map[String, Object]] = {

      val conn = repo.getConnection
      // CHECK: not efficient!
      val results = conn.prepareTupleQuery(QueryLanguage.SPARQL, query)
        .evaluate()
        .toList
        .map { bs =>
          val names = bs.getBindingNames
          names.map { n =>

            val binding = bs.getBinding(n)
            val name = binding.getName
            val value = binding.getValue match {
              case literal: MemLiteral => literal.stringValue()
              case iri: MemIRI         => new URI(iri.stringValue())
              case iri: SimpleIRI      => new URI(iri.stringValue())
              case bnode: MemBNode     => bnode.toString()
              case other               => other.toString()
            }

            (name, value)
          }.toMap
        }

      // TODO: handler
      conn.close()

      results
    }

  }

  // TODO: refactorization
  object helper {

    def addFile(rdfName: String, rdfFile: File, context: String) {

      val default_format = RDFFormat.TURTLE

      val _context = URLDecoder.decode(context, "UTF-8") //.replaceAll("\\s+", "+")
      val format = Rio.getParserFormatForFileName(rdfName).orElse(default_format)

      val fis = new FileInputStream(rdfFile.getAbsoluteFile)
      val ctx = SimpleValueFactory.getInstance.createIRI(_context.trim())

      // adds the file as an RDF document
      val doc = Rio.parse(fis, "", format, ctx)
      _self.store.add(doc, ctx)

      fis.close()
    }

    // TODO: add a configuration 
    def importFrom(rdf_folder: String) {

      val base_path = Paths.get(rdf_folder).toAbsolutePath().normalize()

      logger.debug(s"KB:RDF> importing RDF from ${base_path.toAbsolutePath()}")

      val fs = new FileDatastore(rdf_folder)

      fs.list("owl", "rdf", "ttl", "nt")
        .foreach {
          uri =>

            // CHECK: how to put an ontology in the right context? SEE: configuration

            val format = Rio.getParserFormatForFileName(uri.toString()).get
            val doc = Rio.parse(uri.toURL().openStream(), uri.toString(), format)

            // adds all the namespaces from the file
            val doc_namespaces = doc.getNamespaces.map { ns => (ns.getPrefix, ns.getName) }.toList
            _self.prefixes.add(doc_namespaces: _*)

            val meta = fs.getMetadata(uri)

            if (meta.hasPath("prefix")) {

              // adds the default prefix/namespace pair for this document
              val prefix = meta.getString("prefix")
              val namespace = meta.getString("uri")

              logger.debug(s"\n\nadding ${prefix}:${namespace}")
              _self.prefixes.add((prefix, namespace))

              val contexts_list = meta.getStringList("contexts")
              val contexts = contexts_list.map { cx => vf.createIRI(cx) }

              logger.debug(s"\navailable contexts are: ${contexts.mkString(" | ")}")

              logger.debug(s"importing ${uri} in context ${contexts_list(0)}")

              // adds the document to the default graph (no context!)
              //               REVIEW: 
              //              _self.store.add(doc)

              // adds the document to the contexts provided in .metadata
              _self.store.add(doc, contexts: _*)

            } else {
              logger.warn(s"skipping import of ${uri}: missing meta!")
            }

        }

    }

  }

}