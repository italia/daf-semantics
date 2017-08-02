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
import com.typesafe.config.ConfigFactory

object RDFRepository {

  def remote(endpoint: String) = {
    new RDFRepositoryBase(new SPARQLRepository(endpoint, endpoint))
  }

  def memory() = {

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

  // VERIFY: virtuoso jar dependencies on maven central
  def virtuoso() = {
    // TODO: externalize configurations
    // TODO: add a factory for switching between dev / prod
    val host = "localhost"
    val port = 1111
    val username = "dba"
    val password = "dba"

    val repo = new VirtuosoRepository(s"jdbc:virtuoso://${host}:${port}/charset=UTF-8/log_enable=2", username, password)
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
 * IDEA: use an implicit connection
 * TODO: provide a connection pool
 * TODO: add an update method (remove + add) using the same connection/transaction
 *
 * CHECK: finally (handle connection to be closed) and/or connection pool
 * 	the idea could be encapsulating default behaviours in Try{} object as much as possible
 *
 */
class RDFRepositoryBase(repo: Repository) {

  // CHECK: val logger = LoggerFactory.getLogger(this.getClass)

  val logger = Logger.underlying()

  val _self = this

  // CHECK: providing custom implementation for BN
  var vf: ValueFactory = SimpleValueFactory.getInstance

  // checking if the repository is up.
  def isAlive(): Boolean = {
    try {

      if (!repo.isInitialized())
        repo.initialize()
      val _conn = repo.getConnection
      _conn.close()
      repo.shutDown()
      true

    } catch {
      case ex: Exception =>
        val err_msg = s"KB:RDF> error attempting connection to repository"
        logger.error(err_msg)
        false
    }
  }

  def start() {

    try {

      if (!repo.isInitialized())
        repo.initialize()

      vf = repo.getValueFactory

    } catch {
      case ex: Exception =>
        val err_msg = s"KB:RDF> cannot start repository!"
        logger.error(err_msg)
        throw new RDFRepositoryException(err_msg, ex)
    }

  }

  def stop() {

    try {
      if (repo.isInitialized())
        repo.shutDown()
    } catch {
      case ex: Exception =>
        val err_msg = s"KB:RDF> cannot stop repository!"
        logger.error(err_msg)
        throw new RDFRepositoryException(err_msg, ex)
    }

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
          conn.rollback()
          val err_msg = s"KB:RDF> error while removing namespaces!"
          logger.error(err_msg)
          throw new RDFRepositoryException(err_msg, ex)
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
          conn.rollback()
          val err_msg = s"KB:RDF> cannot add namespaces: ${namespaces}"
          logger.error(err_msg)
          throw new RDFRepositoryException(err_msg, ex)
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
          conn.rollback()
          val err_msg = s"KB:RDF> cannot remove namespaces: ${namespaces}"
          logger.error(err_msg)
          throw new RDFRepositoryException(err_msg, ex)
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
          conn.rollback()
          val err_msg = s"KB:RDF> cannot clear contexts: ${contexts.mkString(", ")}"
          logger.error(err_msg)
          throw new RDFRepositoryException(err_msg, ex)
      }

      conn.close()
    }

    def contexts(): Seq[String] = {

      val conn = repo.getConnection

      var results: Seq[String] = Nil

      try {

        results = conn.getContextIDs.map { ctx => ctx.stringValue() }.toList

      } catch {
        case ex: Exception =>
          val err_msg = s"KB:RDF> cannot retrieve contexts list"
          logger.error(err_msg)
          throw new RDFRepositoryException(err_msg, ex)
      }

      conn.close()

      results
    }

    def size(contexts: Resource*): Long = {
      val conn = repo.getConnection

      val size = if (contexts.size > 0)
        conn.size(contexts: _*)
      else {
        conn.size(null)
      }

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

      val conn = repo.getConnection()
      conn.begin()

      try {

        conn.add(doc, contexts: _*)
        conn.commit()
        logger.debug(s"KB:RDF> ${doc.size()} triples was added to contexts ${contexts.mkString(" | ")}")

      } catch {
        case ex: Exception =>
          conn.rollback()
          val err_msg = s"KB:RDF> cannot add RDF data\n${ex} in ${contexts.mkString("|")}"
          logger.error(err_msg)
          throw new RDFRepositoryException(err_msg, ex)
      }

      conn.close()
    }

    def remove(doc: Model, contexts: Resource*) {

      val conn = repo.getConnection()
      conn.begin()

      try {

        conn.remove(doc, contexts: _*)
        conn.commit()
        logger.debug(s"KB:RDF> ${doc.size()} triples was removed from contexts ${contexts.mkString(" | ")}")

      } catch {
        case ex: Exception =>
          conn.rollback()
          val err_msg = s"KB:RDF> cannot remove RDF data\n${ex}"
          logger.error(err_msg)
          throw new RDFRepositoryException(err_msg, ex)
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

    // TODO: refactorize configurations
    private val _conf = ConfigFactory.parseString("""
      import.formats = [ "owl", "rdf", "ttl", "nt" ]
    """)

    val default_format = RDFFormat.TURTLE

    def addFile(rdfName: String, rdfFile: File, context: String) {

      try {
        val _context = URLDecoder.decode(context, "UTF-8")
        val format = Rio.getParserFormatForFileName(rdfName)
          .orElse(default_format)

        val fis = new FileInputStream(rdfFile.getAbsoluteFile)
        val ctx = SimpleValueFactory.getInstance.createIRI(_context.trim())

        // adds the file as an RDF document
        val doc = Rio.parse(fis, "", format, ctx)
        _self.store.add(doc, ctx)

        fis.close()
      } catch {
        case ex: Exception =>
          val err_msg = s"KB:RDF> cannot add RDF file: ${rdfFile}"
          logger.error(err_msg)
          throw new RDFRepositoryException(err_msg, ex)
      }

    }

    // TODO: add a configuration 
    def importFrom(rdf_folder: String) {

      val base_path = Paths.get(rdf_folder).toAbsolutePath().normalize()

      logger.debug(s"KB:RDF> importing RDF from ${base_path.toAbsolutePath()}")

      val fs = new FileDatastore(rdf_folder)

      fs.list(_conf.getStringList("import.formats"): _*) // TODO: configuration
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

              // adds the document to the contexts provided in .metadata
              _self.store.add(doc, contexts: _*)
              _self.store.add(doc) // also publish to the default context

            } else {
              logger.warn(s"skipping import of ${uri}: missing meta!")
            }

        }

    }

  }

}

class RDFRepositoryException(message: String, cause: Throwable) extends RuntimeException 
