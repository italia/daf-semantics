package it.almawave.kb.repo

import scala.util.Try
import org.slf4j.LoggerFactory
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.model.Value
import org.eclipse.rdf4j.model.IRI
import org.eclipse.rdf4j.model.Resource
import org.eclipse.rdf4j.model.Model
import it.almawave.kb.utils.TryHandlers._
import it.almawave.kb.utils.RDF4JAdapters._

/*
 * this component can be seen as an RDF datastore abstraction
 */
class StoreHelper(repo: Repository) {

  implicit val logger = LoggerFactory.getLogger(this.getClass)

  def clear(contexts: String*) {

    val conn = repo.getConnection
    conn.begin()

    TryLog {

      if (contexts.size > 0) {
        conn.clear(contexts.toIRIList: _*)
      } else {
        // default clear
        conn.clear(null)
        conn.clear()
        // clear each context
        conn.clear(conn.getContextIDs.toList: _*)
      }
      conn.commit()

    }(s"KB:RDF> cannot clear contexts: ${contexts.mkString(", ")}")

    conn.close()
  }

  def contexts(): Try[Seq[String]] = {

    val conn = repo.getConnection

    var results = TryLog {

      conn.getContextIDs.map { ctx => ctx.stringValue() }.toList

    }(s"KB:RDF> cannot retrieve contexts list")

    conn.close()

    results
  }

  def size(contexts: String*): Try[Long] = {

    val conn = repo.getConnection

    val size = TryLog {

      if (contexts.size > 0)
        conn.size(contexts.toIRIList: _*)
      else {
        conn.size(null)
      }

    }(s"can't obtain size for contexts: ${contexts.mkString(" | ")}")

    conn.close()
    size
  }

  def statements(s: Resource, p: IRI, o: Value, inferred: Boolean, contexts: String*) = {

    val conn = repo.getConnection

    // CHECK: not efficient! (reference to stream head!)
    val results = TryLog {

      conn.getStatements(null, null, null, false, contexts.toIRIList: _*).toStream

    }(s"cannot get statements for ${contexts.mkString(" | ")}")

    conn.close()

    results

  }

  def add(doc: Model, contexts: String*) {

    val conn = repo.getConnection()
    conn.begin()

    TryLog {

      conn.add(doc, contexts.toIRIList: _*)
      conn.commit()
      logger.debug(s"KB:RDF> ${doc.size()} triples was added to contexts ${contexts.mkString(" | ")}")

    }(s"KB:RDF> cannot add RDF data in ${contexts.mkString("|")}")

    conn.close()
  }

  def remove(doc: Model, contexts: String*) {

    val conn = repo.getConnection()
    conn.begin()

    TryLog {

      conn.remove(doc, contexts.toIRIList: _*)
      conn.commit()
      logger.debug(s"KB:RDF> ${doc.size()} triples was removed from contexts ${contexts.mkString(" | ")}")

    }(s"KB:RDF> cannot remove RDF data")

    conn.close()
  }

}