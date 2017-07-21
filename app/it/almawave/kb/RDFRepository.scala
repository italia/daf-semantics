package it.almawave.kb

import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.slf4j.LoggerFactory
import org.eclipse.rdf4j.repository.Repository
import java.net.URI
import org.eclipse.rdf4j.model.ValueFactory
import org.eclipse.rdf4j.query.QueryLanguage
import java.util.{ ArrayList => JArrayList }
import org.eclipse.rdf4j.model.Namespace
import java.util.ArrayList
import org.eclipse.rdf4j.query.QueryResults
import org.eclipse.rdf4j.model.IRI
import org.eclipse.rdf4j.rio.Rio
import org.eclipse.rdf4j.model.Statement
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler
import java.nio.file.Paths
import java.nio.file.Files
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository
import org.eclipse.rdf4j.common.iteration.Iterations
import org.eclipse.rdf4j.IsolationLevels
import java.io.InputStream
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.model.Resource
import org.eclipse.rdf4j.query.TupleQuery
import org.eclipse.rdf4j.query.TupleQueryResult

/*
 * TODO: evaluate specific implementation
 * 
 * CHECK: usage of future for results
 */
object RDFRepository {

  def remote(endpoint: String) = {
    new RDFRepository(new SPARQLRepository(endpoint, endpoint))
  }

  def memory() = {
    val repo = new SailRepository(new MemoryStore)
    new RDFRepository(repo)
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
    new RDFRepository(repo)
  }

  /* 
  TODO:
  def virtuoso() = {
    new VirtuosoRepository(s"jdbc:virtuoso://${host}:${port}/charset=UTF-8/log_enable=2", username, password)
  }
  */

}

// TODO: export to a trait
class RDFRepository(repository: Repository) {

  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters._

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val SPARQL = QueryLanguage.SPARQL

  protected val repo: Repository = repository

  // IDEA: add a connection pool!

  val INCLUDE_INFERRED = false

  System.setProperty("org.eclipse.rdf4j.repository.debug", "false")

  def start() {
    logger.info(s"start RDF repository")
    if (!repo.isInitialized())
      repo.initialize()
  }

  def stop() {
    logger.info(s"stop RDF repository")
    if (repo.isInitialized())
      repo.shutDown()
  }

  def loadRDF(rdfDocument: URI, context: String) = {

    val conn = repo.getConnection

    try {

      val graph_uri = new URI(context).normalize()

      val temp_uri = rdfDocument.normalize()
        .toString()
        .replaceAll("(file:/)(\\w+.*)", "$1//$2")

      val rdf_uri = new URI(s"${temp_uri}")

      logger.debug(s"loading data from ${rdfDocument}")

      val query = s"""
			  LOAD <${rdf_uri}> INTO GRAPH <${graph_uri}> 
			"""

      logger.debug(s"SPARQL> executing query\n${query}")

      conn.begin()

      val upd = conn.prepareUpdate(SPARQL, query)
      upd.setIncludeInferred(true)
      upd.setMaxExecutionTime(120000)
      upd.execute()

      logger.debug(s"SPARQL> added ${this.count(context)} triples to context ${context}")
      conn.commit()

    } catch {
      case ex: Exception =>
        logger.error(s"cannot load data from ${rdfDocument}")
        conn.rollback()
        throw new RuntimeException(s"cannot load data from ${rdfDocument}", ex)
    }

    conn.close()

  }

  def existsGraph(context: String): Boolean = {

    val conn = repo.getConnection

    val query = s"""
      ASK { GRAPH <${context}> {} }
    """

    logger.debug(s"SPARQL> executing query\n${query}")
    val result = conn.prepareBooleanQuery(SPARQL, query).evaluate()

    conn.close()

    result

  }

  // gest a list of all graphs
  def graphs() = {

    import it.almawave.kb.Rdf4jAdapters._

    val conn = repo.getConnection

    val query = s"""
      SELECT ?graph
      WHERE {
      	GRAPH ?graph {}	
      }  
    """

    println("\n\n\n\nGRAPHS")
    conn.prepareTupleQuery(SPARQL, query).evaluate()
      .toArray
      .foreach { x => println(x) }
    //      .map { bs => bs.getValue("graph").stringValue() }

    conn.close()

    null
  }

  // DROPS a single graph
  def dropGraph(context: String) = {
    val conn = repo.getConnection
    try {

      conn.begin()
      val query = s"""
        DROP GRAPH <${context}>  
      """
      logger.debug(s"SPARQL> executing query\n${query}")

      conn.prepareUpdate(SPARQL, query).execute()

      conn.commit()

      //      conn.clear(vf.createIRI(context))

      logger.debug(s"graph ${context} dropped")
    } catch {
      case ex: Exception =>
        logger.debug(s"cannot drop graph ${context}\n${ex}")
        conn.rollback()
    }
    conn.close()
  }

  // gets a stream/map representation for the results of a tuple query
  def execute_query(queryString: String): Stream[Map[String, Object]] = {

    logger.debug(s"SPARQL> executing query: ${queryString}")

    val conn = repo.getConnection
    val tupleQuery: TupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString)
    val result: TupleQueryResult = tupleQuery.evaluate()

    val list = new ArrayList[Map[String, Object]]
    while (result.hasNext()) {
      val item: Map[String, Object] = result.next()
        .map { st => (st.getName, st.getValue.stringValue()) }
        .toMap
      list.add(item)
    }

    // TODO: improve with iterators/stream

    conn.close()

    // TODO: check how automatically close the connection?

    list.toStream

  }

  def count(): Long = {
    val conn = repo.getConnection

    val query = s"""
      SELECT (COUNT(*) AS ?triples)
      WHERE { ?s ?p ?O }  
    """
    logger.debug(s"SPARQL> executing query: ${query}")

    val size = conn.prepareTupleQuery(SPARQL, query)
      .evaluate().next()
      .getValue("triples").stringValue().toLong

    conn.close()

    size
  }

  def count(context: String): Long = {

    val conn = repo.getConnection

    val query = s"""
      SELECT (COUNT(*) AS ?triples)
      WHERE {
        GRAPH <${context}> { ?s ?p ?O }
      }  
    """
    val size = conn.prepareTupleQuery(SPARQL, query)
      .evaluate().next()
      .getValue("triples").stringValue().toLong

    conn.close()

    size
  }

  /*
  
  def count(contexts: String*): Long = {

    // REVIEW: with SPARQL

    val conn = repo.getConnection
    val vf = conn.getValueFactory
    val ctx_list: Seq[IRI] = contexts.map { cx => vf.createIRI(cx) }

    val size = QueryResults
      .asModel(conn.getStatements(null, null, null, INCLUDE_INFERRED, ctx_list: _*))
      .size()

    conn.close()

    size

  }
  
  */

  // TODO: add a configuration 
  def importFrom(rdf_folder: String) {

    val base_path = Paths.get(rdf_folder).toAbsolutePath().normalize()

    logger.debug(s"SPARQL> import RDF from ${base_path.toUri()}")

    Files.walk(base_path).iterator().toStream
      .filter(_.toFile().isFile())
      .filter(_.toString().matches(".*\\.(owl|rdf|ttl|nt)"))
      .foreach {
        p =>
          val uri = p.toUri().normalize()
          logger.info(s"importing ${uri}")

          val format = Rio.getParserFormatForFileName(uri.toString()).get
          //          this.addRDF(uri, format, uri.toString())

          this.loadRDF(uri, uri.toString())
      }

  }

  // TODO: query?

  // TODO: RDFHandler

  private def vf: ValueFactory = repo.getValueFactory

  // attempt to clear all the repository
  def clear(contexts: String*) {
    val ctxs: Seq[IRI] = contexts.map { cx => vf.createIRI(cx) }
    val conn = repo.getConnection
    try {
      conn.begin()
      logger.error(s"attempt to clear data")
      conn.clear(ctxs: _*)
      conn.commit()
    } catch {
      case ex: Exception =>
        logger.error(s"error while clear data\n ${ex}")
        conn.rollback()
    }
    conn.close()
  }

  def clearPrefixes() = {
    val conn = repo.getConnection
    conn.clearNamespaces()
    conn.close()
  }

  def prefixes(prefixList: Map[String, String]) {
    val conn = repo.getConnection
    conn.clearNamespaces()
    prefixList.foreach { item => conn.setNamespace(item._1, item._2) }
    conn.close()
  }

  def prefixes() = {

    val conn = repo.getConnection
    val namespaces = conn.getNamespaces

    // TODO: move to an external helper (implicit)
    val list = new ArrayList[Namespace]
    while (namespaces.hasNext())
      list.add(namespaces.next())
    val it = list.iterator()

    conn.close()

    it.map { ns => (ns.getPrefix, ns.getName) }.toMap

  }

  def addPrefix(prefix: String, namespace: String) {
    val conn = repo.getConnection
    conn.setNamespace(prefix, namespace)
    conn.close()
  }

  def removePrefix(prefix: String) = {
    val conn = repo.getConnection
    conn.removeNamespace(prefix)
    conn.close()
  }

  // add RDF content
  def addRDF(input: InputStream, baseURI: URI, format: String, contexts: String*) = {
    val conn = repo.getConnection

    try {

      val ctxs: Seq[IRI] = contexts.map { cx => vf.createIRI(cx) }
      val dataFormat = Rio.getParserFormatForMIMEType(format).orElse(RDFFormat.TURTLE)

      conn.begin()
      conn.add(input, baseURI.toString(), dataFormat, ctxs: _*)
      conn.commit()

    } catch {
      case ex: Exception =>
        logger.debug(s"problems adding triples\n ${ex}")
        conn.rollback()
    }

    conn.close()
  }

  // REVIEW: low-level remove
  def removeRDF(rdfDocument: URI, contexts: String*) = {

    val conn = repo.getConnection
    val vf: ValueFactory = conn.getValueFactory
    val ctxs: Seq[IRI] = contexts.map { cx => vf.createIRI(cx) }

    try {

      conn.begin()

      val url = rdfDocument.toURL()
      val format = Rio.getParserFormatForFileName(rdfDocument.toString()).get

      Rio.parse(url.openStream(), rdfDocument.toString(), format, ctxs: _*)
        .toStream
        .zipWithIndex
        .foreach {
          case (st, i) =>
            println("REMOVING " + i + " " + st)
            conn.remove(st, ctxs: _*)
            conn.commit()
        }

      conn.commit()

      // REVIEW here
      Iterations.asList(conn.getStatements(null, null, null, true, ctxs: _*))
        .foreach { st => println("NOT DELETED! " + st) }

    } catch {
      case ex: Exception =>
        conn.rollback()
        logger.debug(s"problems deleting triples from ${rdfDocument}\n${ex}")
    }

    conn.close()

  }

}

class StatementCounter extends AbstractRDFHandler {

  var count = 0

  override def handleStatement(st: Statement) {
    count += 1
  }

  def counted(): Int = count

}

object PREFIXES {

  import org.eclipse.rdf4j.model.vocabulary._
  import scala.collection.JavaConverters._

  val default = Map(
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

  def setup(repo: Repository) = {

    // IDEA: add lookup from prefix.cc?
    val map = PREFIXES.default.asJava
    val conn = repo.getConnection
    while (conn.getNamespaces.hasNext()) {
      val ns = conn.getNamespaces.next()
      map.put(ns.getPrefix, ns.getName) // IDEA: use URI instead of string?
    }
    conn.close()

    map
  }

}