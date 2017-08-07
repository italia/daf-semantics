package it.almawave.kb.repo

import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.eclipse.rdf4j.repository.sail.SailRepository

import org.slf4j.LoggerFactory
import play.Logger
import org.eclipse.rdf4j.repository.RepositoryConnection

import org.eclipse.rdf4j.repository.Repository
import it.almawave.kb.utils.TryHandlers.TryLog
import java.util.ArrayList
import org.eclipse.rdf4j.model.Resource
import java.net.URL
import org.eclipse.rdf4j.rio.RDFFormat
import it.almawave.kb.utils.TryHandlers.RepositoryAction
import scala.util.Try

// esempio per testing costrutti try/log etc
object TestingBlock extends App {

  implicit val logger = Logger.underlying()
  val repo: Repository = new SailRepository(new MemoryStore)
  repo.initialize()

  // --------------

  val results = RepositoryAction(repo) {

    conn =>

      val vf = conn.getValueFactory

      conn.add(
        new URL("http://xmlns.com/foaf/spec/index.rdf"), "",
        RDFFormat.RDFXML,
        vf.createIRI("http://xmlns.com/foaf/0.1/"))

      logger.info("this is a generic log")

      //TESTING ERRORS: throw new RuntimeException("some problem here!")

      val res = conn.getContextIDs
      val list = new ArrayList[Resource]
      while (res.hasNext()) {
        list.add(res.next())
      }

      list

  }("there was an error...")

  println(results.get)

  //  conn.close()

  // --------------

  repo.shutDown()
}
