
import play._

import javax.inject.Inject
import com.google.inject.{ AbstractModule, Singleton }
import play.api.inject.ApplicationLifecycle
import scala.concurrent.Future
import modules.ClientsModule
import modules.ClientsModuleBase

@Singleton
class Global @Inject() (lifecycle: ApplicationLifecycle) {

  @Inject
  def onStart() {
    Logger.info("#### Application START")
  }

  // REVIEW here
  lifecycle.addStopHook { () =>
    Future.successful({
      Logger.info("#### Application STOP")
    })
  }

  // TODO: plug a servicefactory for repository

}

@Singleton
class StartModule extends AbstractModule {

  def configure() = {

    Logger.info("\n\nCHECKING: StartModule.configure()")

    bind(classOf[ClientsModule]).to(classOf[ClientsModuleBase]).asEagerSingleton()
  }

}

