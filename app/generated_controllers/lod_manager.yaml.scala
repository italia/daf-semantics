
import play.api.mvc.{Action,Controller}

import play.api.data.validation.Constraint

import play.api.i18n.MessagesApi

import play.api.inject.{ApplicationLifecycle,ConfigurationProvider}

import de.zalando.play.controllers._

import PlayBodyParsing._

import PlayValidations._

import scala.util._

import javax.inject._

import java.util.Date
import play.Logger
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * This controller is re-generated after each change in the specification.
 * Please only place your hand-written code between appropriate comments in the body of the controller.
 */

package lod_manager.yaml {
    // ----- Start of unmanaged code area for package Lod_managerYaml
                    
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

    // ----- End of unmanaged code area for package Lod_managerYaml
    class Lod_managerYaml @Inject() (
        // ----- Start of unmanaged code area for injections Lod_managerYaml

        // ----- End of unmanaged code area for injections Lod_managerYaml
        val messagesApi: MessagesApi,
        lifecycle: ApplicationLifecycle,
        config: ConfigurationProvider
    ) extends Lod_managerYamlBase {
        // ----- Start of unmanaged code area for constructor Lod_managerYaml

        // ----- End of unmanaged code area for constructor Lod_managerYaml
        val getCurrentTime = getCurrentTimeAction {  _ =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.getCurrentTime
            val now = new Date()

      Logger.info(s"${this.HOST}/time/now: ${now}")

      GetCurrentTime200(Future {
        Time(1111, now.toString)
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.getCurrentTime
        }
    
    }
}
