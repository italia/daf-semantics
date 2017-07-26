package it.almawave.kb.old

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
import org.eclipse.rdf4j.model.IRI
import org.eclipse.rdf4j.rio.Rio
import java.nio.file.Paths
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository
import org.eclipse.rdf4j.common.iteration.Iterations
import java.io.InputStream
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.query.TupleQuery
import org.eclipse.rdf4j.query.TupleQueryResult
import it.almawave.kb.FileDatastore
import it.almawave.kb.RDFHelper._

/*
 * This is a first naive implementation for providing a basic API over SPARQL.
 * THe idea is to have methods for add/remove ontologies, ask informations, and so on.
 * 
 * TODO: adopt a complete LDP engine, add to that the embedded implementation.
 * TODO: evaluate specific implementation
 * 
 * TODO: refactorization: move SPARQL functions to a specific component 
 * TODO: encapsulates logic for handling Files. FileDatastore?
 * 
 * CHECK: usage of Future for results?
 */

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

  // helper function
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

  def getOntology(ontology_uri: String, format: RDFFormat): String = ???

  // gest a list of all graphs
  def graphs() = {

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
      FROM <${context}> 
      WHERE {
        # GRAPH <${context}> { ?s ?p ?O }
        ?s ?p ?O 
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
