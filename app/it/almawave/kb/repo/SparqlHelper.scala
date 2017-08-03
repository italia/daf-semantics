package it.almawave.kb.repo

import scala.util.Try

import org.slf4j.LoggerFactory
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.query.QueryLanguage

import it.almawave.kb.utils.TryHandlers._
import it.almawave.kb.utils.RDF4JAdapters._

/*
 * this part can be seen as a sparql datastore abstraction
 */
class SPARQLHelper(repo: Repository) {

  implicit val logger = LoggerFactory.getLogger(this.getClass)

  def query(query: String): Try[Seq[Map[String, Object]]] = {

    val conn = repo.getConnection

    val results = TryLog {

      // CHECK: not efficient!
      conn.prepareTupleQuery(QueryLanguage.SPARQL, query)
        .evaluate()
        .toList
        .map(_.toMap())

    }(s"SPARQL> cannot execute query ${query}")

    // TODO: handler
    conn.close()

    results
  }

}