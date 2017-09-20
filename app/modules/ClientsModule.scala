package modules

import com.google.inject.ImplementedBy
import play.api.inject.ApplicationLifecycle
import javax.inject.Singleton
import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.Application
import play.api.Environment
import play.api.Configuration
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import play.Logger
import clients.OntonetHubClient

@ImplementedBy(classOf[ClientsModuleBase])
trait ClientsModule

@Singleton
class ClientsModuleBase @Inject() (lifecycle: ApplicationLifecycle,
                                   ws: WSClient) extends ClientsModule {

  val ontonethub = new OntonetHubClient(ws)

  // when application starts...
  @Inject
  def onStart(
    app: Application,
    env: Environment,
    configuration: Configuration)(implicit ec: ExecutionContext) {

    Logger.info("ClientsModuleBase START")

  }

  // when application stops...
  lifecycle.addStopHook({ () =>

    Future.successful {

      Logger.info("ClientsModuleBase STOP")

    }

  })

}