package it.almawave.linkeddata.kb.repo.managers

import scala.util.Try
import org.slf4j.LoggerFactory
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.query.QueryLanguage
import it.almawave.linkeddata.kb.utils.TryHandlers._
import it.almawave.linkeddata.kb.utils.RDF4JAdapters._
import it.almawave.linkeddata.kb.repo.RepositoryAction
import scala.concurrent.Future

/*
 * this part can be seen as a sparql datastore abstraction
 * 
 * TODO: merge the methods from the initial version
 * 
 */
class SPARQLManager(repo: Repository) {

  implicit val logger = LoggerFactory.getLogger(this.getClass)

  def query(query: String): Try[Seq[Map[String, Object]]] = {

    RepositoryAction(repo) { conn =>

      // CHECK: not efficient!
      conn.prepareTupleQuery(QueryLanguage.SPARQL, query)
        .evaluate()
        .toStream
        .map(_.toMap())
        .toList

    }(s"SPARQL> cannot execute query ${query}")

  }

}