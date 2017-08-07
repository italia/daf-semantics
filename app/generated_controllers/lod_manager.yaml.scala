
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
import de.zalando.play.controllers._
import PlayBodyParsing._
import PlayValidations._
import scala.util._
import scala.concurrent.Future
import java.io.File
import java.io.StringReader
import java.io.FileInputStream
import java.nio.file.Paths
import java.util.Date
import java.net.URLDecoder
import javax.inject._
import play.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action,Controller}
import play.api.inject.{ApplicationLifecycle,ConfigurationProvider}
import play.api.i18n.MessagesApi
import play.api.data.validation.Constraint
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import modules.KBModuleBase
import modules.KBModule
import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.rdf4j.rio.Rio
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.model.Resource
import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import javax.xml.crypto.URIDereferencer

/**
 * This controller is re-generated after each change in the specification.
 * Please only place your hand-written code between appropriate comments in the body of the controller.
 */

package lod_manager.yaml {
    // ----- Start of unmanaged code area for package Lod_managerYaml
            
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
            val namespace = kbrepo.prefixes.list().get(prefix)

      //      val vf = SimpleValueFactory.getInstance // TODO: refactorize here!
      //      val namespace_iri = vf.createIRI(namespace)
      val triples = kbrepo.store.size(namespace).get

      CountTriplesByOntology200(Future {
        TriplesCount(prefix, triples)
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.countTriplesByOntology
        }
        val prefixesList = prefixesListAction {  _ =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.prefixesList
            val prefixes = kbrepo.prefixes.list().get
        .toList
        .map(item => Prefix(item._1, item._2))

      PrefixesList200(Future {
        prefixes
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.prefixesList
        }
        val addRDFDoc = addRDFDocAction { input: (String, File, String, String) =>
            val (name, rdfDocument, prefix, context) = input
            // ----- Start of unmanaged code area for action  Lod_managerYaml.addRDFDoc
            val _context = URLDecoder.decode(context, "UTF-8")
      kbrepo.io.addFile(name, rdfDocument, prefix, _context)

      AddRDFDoc200(Future {
        s"""
          "message": "the document ${rdfDocument.toString()} was correctly added to context ${prefix}:${context}"
        """
      })

      //      NotImplementedYet
            // ----- End of unmanaged code area for action  Lod_managerYaml.addRDFDoc
        }
        val removeRDFDoc = removeRDFDocAction { (context: String) =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.removeRDFDoc
            //      val ctx = SimpleValueFactory.getInstance.createIRI(context.trim())
      kbrepo.store.clear(context)
      RemoveRDFDoc200(Future {
        s"""
          "message": "all the triples in the context ${context} were deleted correctly"
        """
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.removeRDFDoc
        }
        val prefixDirectLookup = prefixDirectLookupAction { (prefix: String) =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.prefixDirectLookup
            lazy val prefixes = kbrepo.prefixes.list()
      val _namespace = prefixes.get(prefix)
      PrefixDirectLookup200(Future {
        Prefix(prefix, _namespace)
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.prefixDirectLookup
        }
        val countTriples = countTriplesAction {  _ =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.countTriples
            val triples = kbrepo.store.size()
      CountTriples200(Future {
        TriplesCount("_ALL_", triples.get)
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.countTriples
        }
        val prefixReverseLookup = prefixReverseLookupAction { (namespace: String) =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.prefixReverseLookup
            val prefixes = kbrepo.prefixes.list().get.map(item => (item._2, item._1))

      val _prefix = prefixes.get(namespace).get
      PrefixReverseLookup200(Future {
        Prefix(_prefix, namespace)
      }) // FIX encode/decode!
            // ----- End of unmanaged code area for action  Lod_managerYaml.prefixReverseLookup
        }
        val contextsList = contextsListAction {  _ =>  
            // ----- Start of unmanaged code area for action  Lod_managerYaml.contextsList
            val vf = SimpleValueFactory.getInstance // TODO: refactorization!
      val _contexts = kbrepo.store.contexts().get
        .map { cx =>
          //          Context(cx, kbrepo.store.size(vf.createIRI(cx)).get)
          Context(cx, kbrepo.store.size(cx).get)
        }
      ContextsList200(Future {
        _contexts
      })
            // ----- End of unmanaged code area for action  Lod_managerYaml.contextsList
        }
    
    }
}
