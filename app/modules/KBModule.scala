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

@ImplementedBy(classOf[KBModuleBase])
trait KBModule

@Singleton
class KBModuleBase @Inject() (lifecycle: ApplicationLifecycle) extends KBModule {

  import scala.concurrent.ExecutionContext.Implicits.global

  @Inject
  def onStart(
    app: Application,
    env: Environment,
    configuration: Configuration)(implicit ec: ExecutionContext) {

    //	  Logger.info("#### Application START")

    Logger.info("KBModuleBase.START....")

  }

  //  TODO:
  lifecycle.addStopHook({ () =>
    Future.successful {
      Logger.info("KBModuleBase.STOP....")
    }

  })

}



