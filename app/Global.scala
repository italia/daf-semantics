
import play._

import javax.inject.Inject
import com.google.inject.{ AbstractModule, Singleton }
import play.api.inject.ApplicationLifecycle
import scala.concurrent.Future

//CHECK: import it.gov.daf.catalogmanager.listeners.{IngestionListener, IngestionListenerImpl}

@Singleton
class Global @Inject() (lifecycle: ApplicationLifecycle) {

  // REVIEW here
  lifecycle.addStopHook { () => Future.successful({}) }

  Logger.info("Global / ApplicationLifecycle")

  def onStart(app: Application) {
    Logger.info("#### Application START")
  }

  def onStop(app: Application) {
    Logger.info("#### Application STOP")
  }

  // TODO: plug a servicefactory for repository

}

@Singleton
class Module extends AbstractModule {
  def configure() = {
    //bind(classOf[IngestionListener]).to(classOf[IngestionListenerImpl]).asEagerSingleton()
  }

}

