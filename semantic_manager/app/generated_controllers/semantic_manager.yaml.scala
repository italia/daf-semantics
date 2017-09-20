
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
        // ----- End of unmanaged code area for injections Semantic_managerYaml
        val messagesApi: MessagesApi,
        lifecycle: ApplicationLifecycle,
        config: ConfigurationProvider
    ) extends Semantic_managerYamlBase {
        // ----- Start of unmanaged code area for constructor Semantic_managerYaml

        // ----- End of unmanaged code area for constructor Semantic_managerYaml
        val listOntologies = listOntologiesAction {  _ =>  
            // ----- Start of unmanaged code area for action  Semantic_managerYaml.listOntologies
            //      val ontonethub = new OntonetHubClient(ws)
      //      ontonethub.list_ontologies.map { 
      //        list => list.as[List[OntologyMeta]]
      //      }

      implicit val ontoJsonFormat = Json.format[OntologyMeta]
      ws.url("http://localhost:8000/stanbol/ontonethub/ontologies")
        .get()
        .map(_.json.as[Array[OntologyMeta]])
        .flatMap { list => ListOntologies200(list) }
        .recoverWith { case err: Throwable => ListOntologies500(Error(err.getMessage)) }
            // ----- End of unmanaged code area for action  Semantic_managerYaml.listOntologies
        }
        val findProperties = findPropertiesAction { input: (String, OntonetHubPropertyRaw, OntologiesPropertiesFindGetLimit) =>
            val (query, lang, limit) = input
            // ----- Start of unmanaged code area for action  Semantic_managerYaml.findProperties
            implicit def toList(root: JsLookupResult): List[JsValue] = root.as[List[JsValue]]

      val hub = new OntonetHubClient(ws)

      val future = hub.find_property(query, lang.getOrElse(""), limit.getOrElse(BigInt.int2bigInt(0)).toInt)

      FindProperties200(future)
        .recoverWith { case err: Throwable => FindProperties500(Error(err.getMessage)) }
            // ----- End of unmanaged code area for action  Semantic_managerYaml.findProperties
        }
    
     // Dead code for absent methodSemantic_managerYaml.addOntologyByPrefix
     /*
      // ----- Start of unmanaged code area for action  Semantic_managerYaml.addOntologyByPrefix
      NotImplementedYet
      // ----- End of unmanaged code area for action  Semantic_managerYaml.addOntologyByPrefix
     */

    
     // Dead code for absent methodSemantic_managerYaml.deleteOntologyByPrefix
     /*
      // ----- Start of unmanaged code area for action  Semantic_managerYaml.deleteOntologyByPrefix
      NotImplementedYet
      // ----- End of unmanaged code area for action  Semantic_managerYaml.deleteOntologyByPrefix
     */

    
    }
}
