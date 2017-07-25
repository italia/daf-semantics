package it.almawave.kb.repo

import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.model.ValueFactory
import org.eclipse.rdf4j.model.IRI
import org.eclipse.rdf4j.model.Value
import org.eclipse.rdf4j.model.Resource
import org.eclipse.rdf4j.model.Model
import org.slf4j.LoggerFactory
import org.eclipse.rdf4j.query.QueryLanguage

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import org.eclipse.rdf4j.query.TupleQueryResultHandler
import java.net.URI
import it.almawave.kb.RDFHelper._

/**
 *
 * TODO: use an implicit connection
 * TODO: provide a connection pool
 * TODO: add an update method (remove + add) using the same connection/transaction
 *
 */
class RDFRepoMock {

  val logger = LoggerFactory.getLogger(this.getClass)

  val repo: Repository = new SailRepository(new MemoryStore())

  // CHECK: providing custom implementation for BN
  var vf: ValueFactory = SimpleValueFactory.getInstance

  def connection = repo.getConnection

  def start() {
    if (!repo.isInitialized())
      repo.initialize()

    vf = repo.getValueFactory
  }

  def stop() {
    if (repo.isInitialized())
      repo.shutDown()
  }

  def prefixes(namespaces: Map[String, String]) {
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

  def prefixes(): Map[String, String] = {

    val conn = repo.getConnection

    val namespaces = conn.getNamespaces.toList
      .map { ns => (ns.getPrefix, ns.getName) }
      .toMap

    conn.close()

    namespaces

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

    import scala.collection.JavaConversions._
    import scala.collection.JavaConverters._

    import org.eclipse.rdf4j.sail.memory.model._

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

}