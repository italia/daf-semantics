
import play.api.mvc.{Action,Controller}

import play.api.data.validation.Constraint

import play.api.i18n.MessagesApi

import play.api.inject.{ApplicationLifecycle,ConfigurationProvider}

import de.zalando.play.controllers._

import PlayBodyParsing._

import PlayValidations._

import scala.util._

import javax.inject._

import play.api.mvc.{Action,Controller}
import play.api.data.validation.Constraint
import play.api.i18n.MessagesApi
import play.api.inject.{ApplicationLifecycle,ConfigurationProvider}
import de.zalando.play.controllers._
import PlayBodyParsing._
import PlayValidations._
import scala.util._
import javax.inject._
import play.api.libs.ws.WSClient
import utilities.JSONHelper
import scala.concurrent.ExecutionContext.Implicits._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.Future
import play.api.libs.json.Json
import play.api.libs.json.JsValue
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import play.api.libs.json.JsLookupResult
import clients.OntonetHubClient
import OntonetHubClient.models._
import OntonetHubClient.models._
import modules.ClientsModuleBase
import OntonetHubClient.models._
import OntonetHubClient.models._
import OntonetHubClient.models._
import OntonetHubClient.models._
import OntonetHubClient.models._
import OntonetHubClient.models._
import OntonetHubClient.models._
import OntonetHubClient.models._
import OntonetHubClient.models._
import OntonetHubClient.models._
import OntonetHubClient.models._

/**
 * This controller is re-generated after each change in the specification.
 * Please only place your hand-written code between appropriate comments in the body of the controller.
 */

package semantic_manager.yaml {
    // ----- Start of unmanaged code area for package Semantic_managerYaml
                                
    // ----- End of unmanaged code area for package Semantic_managerYaml
    class Semantic_managerYaml @Inject() (
        // ----- Start of unmanaged code area for injections Semantic_managerYaml

      ws: WSClient,
      clients: ClientsModuleBase,

        // ----- End of unmanaged code area for injections Semantic_managerYaml
        val messagesApi: MessagesApi,
        lifecycle: ApplicationLifecycle,
        config: ConfigurationProvider
    ) extends Semantic_managerYamlBase {
        // ----- Start of unmanaged code area for constructor Semantic_managerYaml

    import OntonetHubClient.models._
    val ontonethub = clients.ontonethub

        // ----- End of unmanaged code area for constructor Semantic_managerYaml
        val listOntologies = listOntologiesAction {  _ =>  
            // ----- Start of unmanaged code area for action  Semantic_managerYaml.listOntologies
            val hub_config = config.get.underlying.getConfig("clients.ontonethub")

      ontonethub.list_ontologies
        // IDEA for de-coupling from swagger
        //        .map { list => list.map { item => Ontology.unapply(item).get } } 
        //        .map { list => list.map(item => OntologyMeta.tupled(item)) }
        .flatMap { list => ListOntologies200(list) }
        .recoverWith { case err: Throwable => ListOntologies500(Error(err.getMessage)) }
            // ----- End of unmanaged code area for action  Semantic_managerYaml.listOntologies
        }
        val findProperties = findPropertiesAction { input: (String, OntologiesPropertiesFindGetLang, OntologiesPropertiesFindGetLimit) =>
            val (query, lang, limit) = input
            // ----- Start of unmanaged code area for action  Semantic_managerYaml.findProperties
            ontonethub.find_property(query, lang.getOrElse(""), limit.getOrElse(BigInt.int2bigInt(10)).toInt)
        // de-coupling from swagger
        //        .map { list => list.map { item => FindResult.unapply(item).get } }
        //        .map { list => list.map { item => OntonetHubProperty.tupled(item) } }
        .flatMap { item => FindProperties200(item) }
        .recoverWith { case err: Throwable => FindProperties500(Error(err.getMessage)) }
            // ----- End of unmanaged code area for action  Semantic_managerYaml.findProperties
        }
    
    }
}
