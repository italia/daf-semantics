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
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions

@ImplementedBy(classOf[ClientsModuleBase])
trait ClientsModule

@Singleton
class ClientsModuleBase @Inject() (lifecycle: ApplicationLifecycle,
                                   ws: WSClient,
                                   configuration: Configuration) extends ClientsModule {

  val conf_clients = configuration.underlying
    .getConfig("clients")

  val ontonethub_config = conf_clients.getConfig("ontonethub")

  // TODO: verify if default configurations are needed here
  val ontonethub = new OntonetHubClient(ws, ontonethub_config)

  // TESTING ................................................
  val options = ConfigRenderOptions.concise()
    .setComments(false).setOriginComments(false)
    .setFormatted(true).setJson(true)
  val json = ontonethub_config.root().render(options)
  // TESTING ................................................

  // when application starts...
  @Inject
  def onStart(
    app: Application,
    env: Environment)(implicit ec: ExecutionContext) {

    Logger.info("ClientsModuleBase START")

    println("\n\n\n\n\n\n")
    println(json)

  }

  // when application stops...
  lifecycle.addStopHook({ () =>

    Future.successful {

      Logger.info("ClientsModuleBase STOP")

    }

  })

}