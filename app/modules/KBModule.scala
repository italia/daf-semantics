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
import it.almawave.kb.ConfigHelper
import it.almawave.kb.repo._
import scala.concurrent.ExecutionContext.Implicits.global
import java.nio.file.Paths
import play.api.Mode
import java.io.File

@ImplementedBy(classOf[KBModuleBase])
trait KBModule

@Singleton
class KBModuleBase @Inject() (lifecycle: ApplicationLifecycle) extends KBModule {

  //  val kbrepo = RDFRepository.memory()
  val kbrepo = RDFRepository.virtuoso()

  // when application starts...
  @Inject
  def onStart(
    app: Application,
    env: Environment,
    configuration: Configuration)(implicit ec: ExecutionContext) {

    val conf = configuration.getConfig("kb").get.underlying
    Logger.info("KBModule.config")
    Logger.debug("KBModule.using configuration:\n" + ConfigHelper.pretty(conf))

    Logger.info("KBModule.START....")

    // this is needed for ensure proper connection(s) etc
    kbrepo.start()

    // reset prefixes to default ones
    kbrepo.prefixes.clear()
    kbrepo.prefixes.add(kbrepo.prefixes.DEFAULT.toList: _*)

    kbrepo.helper.importFrom(conf.getString("cache"))

    // CHECK the initial (total) triples count
    var triples = kbrepo.store.size()

    Logger.info(s"KBModule> ${triples} triples loaded")

  }

  // when application stops...
  lifecycle.addStopHook({ () =>

    Future.successful {

      // this is useful for saving files, closing connections, release indexes, etc
      kbrepo.stop()
      Logger.info("KBModule.STOP....")

    }

  })

}

