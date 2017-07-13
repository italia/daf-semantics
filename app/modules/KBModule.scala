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
import it.almawave.linkeddata.kb.RDFRepo

@ImplementedBy(classOf[KBModuleBase])
trait KBModule

@Singleton
class KBModuleBase @Inject() (lifecycle: ApplicationLifecycle) extends KBModule {

  import scala.concurrent.ExecutionContext.Implicits.global

  val kbrepo = RDFRepo.inMemory("RDF_CACHE")

  @Inject
  def onStart(
    app: Application,
    env: Environment,
    configuration: Configuration)(implicit ec: ExecutionContext) {

    val rdf_folder = "C:/Users/Al.Serafini/awavedev/workspace_playground/kb-core/ontologies"

    Logger.info("KBModuleBase.START....")

    kbrepo.start()
    kbrepo.importFrom(rdf_folder)

    val triples = kbrepo.triplesCount()

    Logger.info(s"KBModuleBase: ${triples} triples loaded")

  }

  //  TODO:
  lifecycle.addStopHook({ () =>

    Future.successful {

      kbrepo.stop()
      Logger.info("KBModuleBase.STOP....")

    }

  })

}



