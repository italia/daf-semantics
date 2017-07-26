
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
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.rio.Rio
import org.eclipse.rdf4j.rio.RDFFormat
import java.io.StringReader
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

    val kbrepo = kb.kbrepo

        // ----- End of unmanaged code area for constructor Lod_managerYaml
        val countTriplesByOntology = countTriplesByOntologyAction { (prefix: String) =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.countTriplesByOntology
            val namespace = kbrepo.prefixes.list().get(prefix).get
      println("countTriplesByOntology : " + namespace)
      // CHECK: it can't work, actually! at the moment local documents are published on a context reflecting path! 

      val triples = kbrepo.store.sizeByContexts(Array(namespace)) // TODO: refactorize

      CountTriplesByOntology200(Future {
        TriplesCount(prefix, triples)
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.countTriplesByOntology
        }
        val prefixesList = prefixesListAction {  _ =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.prefixesList
            val prefixes = kbrepo.prefixes.list().toList.map(item => Prefix(item._1, item._2))

      PrefixesList200(Future {
        prefixes
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.prefixesList
        }
        val countTriples = countTriplesAction {  _ =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.countTriples
            val triples = kbrepo.store.size()

      CountTriples200(Future {
        TriplesCount("_ALL_", triples)
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.countTriples
        }
        val prefixReverseLookup = prefixReverseLookupAction { (namespace: String) =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.prefixReverseLookup
            val prefixes = kbrepo.prefixes.list().map(item => (item._2, item._1))

      val _prefix = prefixes.get(namespace).get
      PrefixReverseLookup200(Future {
        Prefix(_prefix, namespace)
      }) // FIX encode/decode!
            // ----- End of unmanaged code area for action  Lod_managerYaml.prefixReverseLookup
        }
        val prefixDirectLookup = prefixDirectLookupAction { (prefix: String) =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.prefixDirectLookup
            lazy val prefixes = kbrepo.prefixes.list()

      val _namespace = prefixes.get(prefix).get
      PrefixDirectLookup200(Future {
        Prefix(prefix, _namespace)
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.prefixDirectLookup
        }
        val invokeUsingPOST = invokeUsingPOSTAction { input: (String, String) =>
            val (context, document) = input
            // ----- Start of unmanaged code area for action  Lod_managerYaml.invokeUsingPOST
            // something like....
      //      val vf = SimpleValueFactory.getInstance
      //      val ctx = vf.createIRI(context)
      //      val doc = Rio.parse(new StringReader(document), "", RDFFormat.TURTLE)
      //      kbrepo.store.add(doc, ctx)
      //      InvokeUsingPOST200(Future {
      //        "{OK}"
      //      })

      NotImplementedYet
            // ----- End of unmanaged code area for action  Lod_managerYaml.invokeUsingPOST
        }
    
    }
}
