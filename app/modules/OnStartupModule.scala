package modules

import javax.inject.Inject
import javax.inject.Singleton
import play.api.Application
import play.Logger
import play.Configuration
import play.Environment
import scala.io.Source
import scala.concurrent.ExecutionContext
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import check.DummyRepo
import it.almawave.linkeddata.kb.RDFRepo

import scala.collection.JavaConverters._
import scala.collection.JavaConversions._

/*
 * REMOVE: module for manual experiments / testing
 */
@Singleton
class OnStartupModule {

  @Inject
  def start(
    app: Application,
    env: Environment,
    configuration: Configuration
  )(implicit ec: ExecutionContext) {

    Logger.info("#### OnStartupModule.START")

    val conf = configuration.getConfig("kb")

    // print a list of configured metadata for ontologies
    Logger.debug("#### ONTOLOGIES....")
    conf.getConfig("ontologies").keys()
      .foreach { k => Logger.debug(k) }

    //    REVIEW HERE ----------------------------------------------------------------------------
    val dir_ontologies = Paths.get(conf.getString("cache")).toFile()
    Logger.debug("\n\n\n\n CACHE DIRECTORY: " + dir_ontologies)

    // TODO: check logging!

    @Inject
    val repo: RDFRepo = RDFRepo.inMemory()
    // TEST val repo = new DummyRepo
    repo.start()
    repo.importFrom(dir_ontologies.toString()) // CHECK: evaluate using Paths

    val triples = repo.triplesCount()
    Logger.info(s"loaded ${triples} triples...")

    // CHECK: how to use repo.shutdown?

    //    REVIEW HERE ----------------------------------------------------------------------------

  }

}