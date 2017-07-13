
import play._

import javax.inject.Inject
import com.google.inject.{ AbstractModule, Singleton }
import play.api.inject.ApplicationLifecycle
import scala.concurrent.Future
import modules.OnStartupModule
import modules.KBModuleBase
import modules.KBModule

//CHECK: import it.gov.daf.catalogmanager.listeners.{IngestionListener, IngestionListenerImpl}

@Singleton
class Global @Inject() (
    lifecycle: ApplicationLifecycle) {

  // REVIEW here
  //  lifecycle.addStopHook { () =>
  //    Future.successful({
  //      println("#### Application STOP")
  //    })
  //  }

  // TODO: plug a servicefactory for repository

}

@Singleton
class StartModule extends AbstractModule {

  def configure() = {

    println("\n\n\n\nCHECKING: StartModule.configure()")

    //    bind(classOf[OnStartupModule]).asEagerSingleton()

    bind(classOf[KBModule]).to(classOf[KBModuleBase]).asEagerSingleton()
  }

}

