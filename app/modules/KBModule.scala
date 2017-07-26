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
//import it.almawave.linkeddata.kb.RDFRepo
import it.gov.daf.lodmanager.utility.ConfigHelper
import it.almawave.kb.repo.RDFRepoMock

@ImplementedBy(classOf[KBModuleBase])
trait KBModule

@Singleton
class KBModuleBase @Inject() (lifecycle: ApplicationLifecycle) extends KBModule {

  import scala.concurrent.ExecutionContext.Implicits.global

  //  lazy val kbrepo = RDFRepository.memory() // CHECK HERE

  import it.almawave.kb.repo._

  val kbrepo = RDFRepository.memory()

  @Inject
  def onStart(
    app: Application,
    env: Environment,
    configuration: Configuration)(implicit ec: ExecutionContext) {

    val conf = configuration.getConfig("kb").get.underlying
    Logger.info("KBModuleBase.config")
    Logger.info(ConfigHelper.pretty(conf))

    val rdf_folder = conf.getString("cache")

    Logger.info("KBModuleBase.START....")

    // this is needed for ensure proper connection(s) etc
    kbrepo.start()

    // reset prefixes to default ones
    kbrepo.prefixes.clear()
    kbrepo.prefixes.add(kbrepo.prefixes.DEFAULT.toList: _*)

    // importing from local files
    kbrepo.helper.importFrom(rdf_folder)

    // CHECK the initial (total) triples count
    val triples = kbrepo.store.size()
    Logger.info(s"KBModuleBase: ${triples} triples loaded")

  }

  // TODO:
  lifecycle.addStopHook({ () =>

    Future.successful {

      // this is useful for saving files, closing connection ,etc
      kbrepo.stop()
      Logger.info("KBModuleBase.STOP....")

    }

  })

}

