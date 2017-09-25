
import play.api.mvc.{Action,Controller}

import play.api.data.validation.Constraint

import play.api.i18n.MessagesApi

import play.api.inject.{ApplicationLifecycle,ConfigurationProvider}

import de.zalando.play.controllers._

import PlayBodyParsing._

import PlayValidations._

import scala.util._

import javax.inject._

import java.io.File

import scala.collection.JavaConversions._
import play.api.inject.ApplicationLifecycle
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import modules.ValidatorModuleBase
import com.google.common.collect.Multimaps.AsMap
import play.Configuration
import play.api.Logger

/**
 * This controller is re-generated after each change in the specification.
 * Please only place your hand-written code between appropriate comments in the body of the controller.
 */

package semantic_validator.yaml {
    // ----- Start of unmanaged code area for package Semantic_validatorYaml
                                                                                                                                                                                                                                    
    // ----- End of unmanaged code area for package Semantic_validatorYaml
    class Semantic_validatorYaml @Inject() (
        // ----- Start of unmanaged code area for injections Semantic_validatorYaml
      vmb: ValidatorModuleBase,
        // ----- End of unmanaged code area for injections Semantic_validatorYaml
        val messagesApi: MessagesApi,
        lifecycle: ApplicationLifecycle,
        config: ConfigurationProvider
    ) extends Semantic_validatorYamlBase {
        // ----- Start of unmanaged code area for constructor Semantic_validatorYaml

        // ----- End of unmanaged code area for constructor Semantic_validatorYaml
        val doValidation = doValidationAction { input: (String, File, String, Boolean) =>
            val (name, rdfDocument, validator, rdfsinf) = input
            // ----- Start of unmanaged code area for action  Semantic_validatorYaml.doValidation
            //NotImplementedYet

      val v = vmb.doValidation(name, rdfDocument, validator, rdfsinf);

      DoValidation200(Future {
        v
      })
            // ----- End of unmanaged code area for action  Semantic_validatorYaml.doValidation
        }
        val getValidators = getValidatorsAction {  _ =>  
            // ----- Start of unmanaged code area for action  Semantic_validatorYaml.getValidators
            //NotImplementedYet

      var lc = vmb.getValidators()

      GetValidators200(Future {
        lc
      })
            // ----- End of unmanaged code area for action  Semantic_validatorYaml.getValidators
        }
    
    }
}
