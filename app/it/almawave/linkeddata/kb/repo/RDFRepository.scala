package it.almawave.linkeddata.kb.repo

import java.io.File
import java.io.FileInputStream
import java.net.URLDecoder
import java.nio.file.Paths
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.asScalaSet
import org.eclipse.rdf4j.model.ValueFactory
import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.rio.Rio
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.slf4j.LoggerFactory
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import it.almawave.linkeddata.kb.utils.RDF4JAdapters.StringContextAdapter
import it.almawave.linkeddata.kb.utils.TryHandlers.TryLog
import it.almawave.linkeddata.kb.repo.managers.RDFFileManager
import it.almawave.linkeddata.kb.repo.managers.RDFStoreManager
import it.almawave.linkeddata.kb.repo.managers.PrefixesManager
import it.almawave.linkeddata.kb.repo.managers.SPARQLManager
import scala.concurrent.Future
import it.almawave.linkeddata.kb.utils.TryHandlers.FutureWithLog
import scala.util.Try

object RDFRepository {

  val logger = LoggerFactory.getLogger(this.getClass)

  def memory() = {

    val mem = new MemoryStore
    val repo: Repository = new SailRepository(mem)
    new RDFRepositoryBase(repo)

  }

}

class RDFRepositoryBase(repo: Repository) {

  //  val logger = Logger.underlying()

  implicit val logger = LoggerFactory.getLogger(this.getClass)

  // CHECK: providing custom implementation for BN

  private var conf = ConfigFactory.empty()

  def configuration(configuration: Config) = {
    conf = conf.withFallback(configuration)
  }

  def configuration(): Config = conf

  // checking if the repository is up.
  def isAlive(): Try[Boolean] = {

    TryLog {

      if (!repo.isInitialized()) repo.initialize()
      repo.getConnection.close()
      repo.shutDown()
      true

    }("repository is not reachable!")

  }

  def start() = {

    TryLog {

      if (!repo.isInitialized())
        repo.initialize()

    }(s"KB:RDF> cannot start repository!")

  }

  def stop() = {

    TryLog {

      if (repo.isInitialized())
        repo.shutDown()

    }(s"KB:RDF> cannot stop repository!")

  }

  val prefixes = new PrefixesManager(repo)
  val store = new RDFStoreManager(repo)

  val sparql = new SPARQLManager(repo)

  val io = new RDFFileManager(this)

}
