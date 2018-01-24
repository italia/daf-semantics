package modules

import javax.inject._

import play.api.inject.ApplicationLifecycle
import play.api.mvc._

import scala.concurrent.Future
import com.google.inject.ImplementedBy
import play.api.Play
import play.api.Application
import play.api.Environment
import play.api.Configuration
import scala.concurrent.ExecutionContext
import play.api.Logger
import it.almawave.linkeddata.kb.utils.ConfigHelper
import it.almawave.linkeddata.kb.repo._
import scala.concurrent.ExecutionContext.Implicits.global
import java.nio.file.Paths
import play.api.Mode
import java.io.File
import it.almawave.linkeddata.kb.repo.RDFRepository
import com.typesafe.config.ConfigFactory

@ImplementedBy(classOf[KBModuleBase])
trait KBModule

@Singleton
class KBModuleBase @Inject() (lifecycle: ApplicationLifecycle) extends KBModule {

  // TODO: SPI per dev / prod
  val kbrepo = RDFRepository.memory()

  val logger = Logger.underlyingLogger

  // when application starts...
  @Inject
  def onStart(
    env: Environment,
    configuration: Configuration)(implicit ec: ExecutionContext) {

    // get configs
    val app_type = configuration.underlying.getString("app.type")

    val data_dir = app_type match {
      case "dev"  => "./dist/data"
      case "prod" => "./data"
    }
    logger.debug(s"app_type: ${app_type}")
    logger.debug(s"data_dir: ${data_dir}")

    // starting VocabularyAPI service
    var conf_voc = ConfigFactory.parseFile(new File("./conf/semantic_repository.conf").getAbsoluteFile)
    conf_voc = ConfigHelper.injectParameters(conf_voc, ("data_dir", data_dir))

    kbrepo.configuration(conf_voc)

    logger.info("KBModule.START....")
    logger.debug("KBModule using configuration:\n" + ConfigHelper.pretty(conf_voc))

    println("KBModule using configuration:\n" + ConfigHelper.pretty(conf_voc))

    // this is needed for ensure proper connection(s) etc
    kbrepo.start()

    /* LOADING temporarly disabled for testing
    // reset prefixes to default ones
    kbrepo.prefixes.clear()
    kbrepo.prefixes.add(kbrepo.prefixes.DEFAULT.toList: _*)

    // CHECK for pre-loading of ontologies
    val kbconf = conf_voc.getConfig("kb")
    if (kbconf.hasPath("cache"))
      kbrepo.io.importFrom(kbconf.getString("cache"))
		*/

    // CHECK the initial (total) triples count
    var triples = kbrepo.store.size()

    logger.info(s"KBModule> ${triples} triples loaded")

  }

  // when application stops...
  lifecycle.addStopHook({ () =>

    Future.successful {

      // this is useful for saving files, closing connections, release indexes, etc
      kbrepo.stop()
      logger.info("KBModule.STOP....")

    }

  })

}

