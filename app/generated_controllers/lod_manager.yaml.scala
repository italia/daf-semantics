
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
import it.gov.daf.lodmanager.service.ServiceRegistry
import modules.KBModuleBase
import modules.KBModule
import play.api.libs.json.Json
import com.fasterxml.jackson.databind.ObjectMapper
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * This controller is re-generated after each change in the specification.
 * Please only place your hand-written code between appropriate comments in the body of the controller.
 */

package lod_manager.yaml {
    // ----- Start of unmanaged code area for package Lod_managerYaml
            
  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters._
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

    // ----- End of unmanaged code area for package Lod_managerYaml
    class Lod_managerYaml @Inject() (
        // ----- Start of unmanaged code area for injections Lod_managerYaml

      kb: KBModuleBase,

        // ----- End of unmanaged code area for injections Lod_managerYaml
        val messagesApi: MessagesApi,
        lifecycle: ApplicationLifecycle,
        config: ConfigurationProvider
    ) extends Lod_managerYamlBase {
        // ----- Start of unmanaged code area for constructor Lod_managerYaml

        // ----- End of unmanaged code area for constructor Lod_managerYaml
        val countTriplesByOntology = countTriplesByOntologyAction { (prefix: String) =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.countTriplesByOntology
            val kbrepo = kb.kbrepo

      val namespace = kbrepo.prefixes().get(prefix).get
      lazy val triples = kbrepo.count(namespace)

      CountTriplesByOntology200(Future {
        TriplesCount(prefix, triples)
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.countTriplesByOntology
        }
        val prefixesList = prefixesListAction {  _ =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.prefixesList
            val kbrepo = kb.kbrepo
      lazy val prefixes = kbrepo.prefixes().toSeq.map(item => Prefix(item._1, item._2))

      PrefixesList200(Future {
        prefixes
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.prefixesList
        }
        val getOntology = getOntologyAction { (prefix: String) =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.getOntology
            val kbrepo = kb.kbrepo

      val namespace = kbrepo.prefixes().get(prefix).get

      // TODO: we need a policy for using named graphs / context...
      val query = s"""
      SELECT * 
      WHERE {
          ?subject a ?concept .
      }
      """

      logger.debug(s"""SPARQL> execute query\n${query}""")

      // TODO: check play/json or install jackson/scala module
      val json_mapper = new ObjectMapper
      val json_writer = json_mapper.writerWithDefaultPrettyPrinter()

      val result = kbrepo.execute_query(query)

      GetOntology200(Future {
        result.mkString("\n")
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.getOntology
        }
        val sparql = sparqlAction {  _ =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.sparql
            NotImplementedYet
            // ----- End of unmanaged code area for action  Lod_managerYaml.sparql
        }
        val countTriples = countTriplesAction {  _ =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.countTriples
            val kbrepo = kb.kbrepo
      lazy val triples = kbrepo.count()

      CountTriples200(Future {
        TriplesCount("_ALL_", triples)
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.countTriples
        }
        val prefixReverseLookup = prefixReverseLookupAction { (namespace: String) =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.prefixReverseLookup
            val kbrepo = kb.kbrepo
      lazy val prefixes = kbrepo.prefixes().map { item => (item._2, item._1) }

      val _prefix = prefixes.get(namespace).get
      PrefixReverseLookup200(Future {
        Prefix(_prefix, namespace)
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.prefixReverseLookup
        }
        val prefixDirectLookup = prefixDirectLookupAction { (prefix: String) =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.prefixDirectLookup
            val kbrepo = kb.kbrepo
      lazy val prefixes = kbrepo.prefixes()

      val _namespace = prefixes.get(prefix).get
      PrefixDirectLookup200(Future {
        Prefix(prefix, _namespace)
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.prefixDirectLookup
        }
    
    }
}
