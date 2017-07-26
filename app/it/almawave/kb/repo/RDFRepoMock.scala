package it.almawave.kb.repo

import java.net.URI
import java.nio.file.Paths

import scala.collection.JavaConversions.asScalaSet

import org.eclipse.rdf4j.model.IRI
import org.eclipse.rdf4j.model.Model
import org.eclipse.rdf4j.model.Resource
import org.eclipse.rdf4j.model.Value
import org.eclipse.rdf4j.model.ValueFactory
import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.model.vocabulary.DC
import org.eclipse.rdf4j.model.vocabulary.DOAP
import org.eclipse.rdf4j.model.vocabulary.FN
import org.eclipse.rdf4j.model.vocabulary.FOAF
import org.eclipse.rdf4j.model.vocabulary.GEO
import org.eclipse.rdf4j.model.vocabulary.OWL
import org.eclipse.rdf4j.model.vocabulary.RDF
import org.eclipse.rdf4j.model.vocabulary.RDFS
import org.eclipse.rdf4j.model.vocabulary.SD
import org.eclipse.rdf4j.model.vocabulary.SKOS
import org.eclipse.rdf4j.model.vocabulary.XMLSchema
import org.eclipse.rdf4j.query.QueryLanguage
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository
import org.eclipse.rdf4j.rio.Rio
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.slf4j.LoggerFactory

import it.almawave.kb.FileDatastore
import it.almawave.kb.RDFHelper.RepositoryResultIterator
import it.almawave.kb.RDFHelper.TupleResultIterator
import java.nio.file.Files
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config

object RDFRepository {

  def remote(endpoint: String) = {
    new RDFRepoMock(new SPARQLRepository(endpoint, endpoint))
  }

  def memory() = {

    // CHECK: how to handle contexts properly

    val mem = new MemoryStore
    val repo = new SailRepository(mem)
    new RDFRepoMock(repo)
  }

  // TODO: config
  def memory(dir_cache: String = "target/data/rdf_cache") = {

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
    new RDFRepoMock(repo)
  }

  /* 
  TODO:
  def virtuoso() = {
    new VirtuosoRepository(s"jdbc:virtuoso://${host}:${port}/charset=UTF-8/log_enable=2", username, password)
  }
  */

  /* DISABLED
  def solr() {

    val index = new SolrIndex()
    val sailProperties = new Properties()
    sailProperties.put(SolrIndex.SERVER_KEY, "embedded:")
    index.initialize(sailProperties)
    val client = index.getClient()

    val memoryStore = new MemoryStore()
    // enable lock tracking
    org.eclipse.rdf4j.common.concurrent.locks.Properties.setLockTrackingEnabled(true)
    val lucenesail = new LuceneSail()
    lucenesail.setBaseSail(memoryStore)
    lucenesail.setLuceneIndex(index)

    val repo = new SailRepository(lucenesail)

  }
  
  */

}

trait RDFRepository {

  var repo: Repository = new SailRepository(new MemoryStore)

  def start()

  def stop()

  def apply(repository: Repository) {
    this.repo = repository
  }

}

/**
 *
 * TODO: use an implicit connection
 * TODO: provide a connection pool
 * TODO: add an update method (remove + add) using the same connection/transaction
 *
 */
class RDFRepoMock(repo: Repository) extends RDFRepository {

  val logger = LoggerFactory.getLogger(this.getClass)

  val _self = this

  //  val repo: Repository = new SailRepository(new MemoryStore())

  // CHECK: providing custom implementation for BN
  var vf: ValueFactory = SimpleValueFactory.getInstance

  //  def connection = repo.getConnection

  def start() {
    if (!repo.isInitialized())
      repo.initialize()

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

    // set prefixes
    def set(namespaces: Map[String, String]) {
      val conn = repo.getConnection
      conn.begin()
      try {
        conn.clearNamespaces()
        namespaces.foreach { pair => conn.setNamespace(pair._1, pair._2) }
        conn.commit()
      } catch {
        case ex: Exception =>
          logger.error(s"KB:RDF> cannot update namespaces: ${namespaces}")
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

  /*
   * this component can be seen as an RDF datastore abstraction
   */
  object store {

    def clear(contexts: Resource*) {

      val conn = repo.getConnection
      conn.begin()

      try {
        conn.clear(contexts: _*)
        conn.commit()
      } catch {
        case ex: Exception =>
          logger.error(s"KB:RDF> cannot clear contexts: ${contexts.mkString(", ")}")
          conn.rollback()
      }

      conn.close()

    }

    def contexts(): Seq[String] = {

      val conn = repo.getConnection
      val results: Seq[String] = conn.getContextIDs.map { ctx => ctx.stringValue() }.toList
      conn.close()

      results
    }

    // TODO: refactorize / merge the two signatures!
    def sizeByContexts(contexts: Seq[String]): Long = {
      val ctxs = contexts.map { cx => vf.createIRI(cx) }.toList
      this.size(ctxs: _*)
    }

    def size(contexts: Resource*): Long = {
      val conn = repo.getConnection

      var size = conn.size(contexts: _*) // CHECK: blank nodes!

      // CHECK for consistencies, with unit tests!
      //      val size2 = this.statements(null, null, null, false, contexts: _*).size
      //      val size2 =  conn.getStatements(null, null, null, false, contexts: _*).size
      //      val size3 = this.statements(null, null, null, false).size

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
      val ctxs = doc.contexts().toSeq.union(contexts.toSeq).distinct

      val conn = repo.getConnection()
      conn.begin()

      try {
        conn.add(doc, ctxs: _*)
        conn.commit()
      } catch {
        case ex: Exception =>
          logger.debug(s"KB:RDF> cannot add RDF data\n${ex}")
          conn.rollback()
      }

      conn.close()
    }

    def remove(doc: Model, contexts: Resource*) {

      // merge the contexts
      val ctxs = doc.contexts().toSeq.union(contexts.toSeq).distinct

      val conn = repo.getConnection()
      conn.begin()

      try {
        conn.remove(doc, ctxs: _*)
        conn.commit()
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

    import org.eclipse.rdf4j.sail.memory.model._
    import scala.collection.JavaConversions._

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
              case bnode: MemBNode     => bnode.toString()
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

    import scala.collection.JavaConversions._
    import scala.collection.JavaConverters._

    // TODO: add a configuration 
    def importFrom(rdf_folder: String) {

      val base_path = Paths.get(rdf_folder).toAbsolutePath().normalize()

      logger.debug(s"SPARQL> import RDF from ${base_path.toUri()}")

      val fs = new FileDatastore(rdf_folder)

      fs.list("owl", "rdf", "ttl", "nt")
        .foreach {
          uri =>

            // CHECK: how to put an ontology in the right context? SEE: configuration

            val format = Rio.getParserFormatForFileName(uri.toString()).get
            val doc = Rio.parse(uri.toURL().openStream(), uri.toString(), format)

            val meta = fs.getMetadata(uri)

            if (meta.hasPath("prefix")) {

              val contexts_list = meta.getStringList("contexts")
              val contexts = contexts_list.map { cx => vf.createIRI(cx) }

              println("import " + uri)
              logger.info(s"importing ${uri} in context ${contexts_list(0)}")

              _self.store.add(doc, contexts: _*)

            } else {
              logger.warn(s"skipping import of ${uri}: missing meta!")
            }

        }

    }

  }

}