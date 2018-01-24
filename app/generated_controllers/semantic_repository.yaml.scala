
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

import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient
import modules.KBModuleBase
import java.net.URLDecoder
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WSClient

/**
 * This controller is re-generated after each change in the specification.
 * Please only place your hand-written code between appropriate comments in the body of the controller.
 */

package semantic_repository.yaml {
    // ----- Start of unmanaged code area for package Semantic_repositoryYaml
                                                                                                                                                                                                                                                                                                                                                                            
  import modules.KBModuleBase
  import java.net.URLDecoder
  import scala.concurrent.Future
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  import play.api.libs.ws.WSClient

    // ----- End of unmanaged code area for package Semantic_repositoryYaml
    class Semantic_repositoryYaml @Inject() (
        // ----- Start of unmanaged code area for injections Semantic_repositoryYaml

      kb: KBModuleBase,
      ws: WSClient,

        // ----- End of unmanaged code area for injections Semantic_repositoryYaml
        val messagesApi: MessagesApi,
        lifecycle: ApplicationLifecycle,
        config: ConfigurationProvider
    ) extends Semantic_repositoryYamlBase {
        // ----- Start of unmanaged code area for constructor Semantic_repositoryYaml

    // wrapper for triplestore
    val kbrepo = kb.kbrepo

        // ----- End of unmanaged code area for constructor Semantic_repositoryYaml
        val prefixesList = prefixesListAction {  _ =>  
            // ----- Start of unmanaged code area for action  Semantic_repositoryYaml.prefixesList
            val prefixes = kbrepo.prefixes.list().get
        .toList
        .map(item => Prefix(item._1, item._2))

      PrefixesList200(prefixes)
        .recoverWith {
          case err: Throwable => PrefixesList500(Error("problems retrieving prefixes list!", err.getMessage))
        }
            // ----- End of unmanaged code area for action  Semantic_repositoryYaml.prefixesList
        }
        val prefixDirectLookup = prefixDirectLookupAction { (prefix: String) =>  
            // ----- Start of unmanaged code area for action  Semantic_repositoryYaml.prefixDirectLookup
            lazy val prefixes = kbrepo.prefixes.list()
      val _namespace = prefixes.get(prefix)

      PrefixDirectLookup200(Prefix(prefix, _namespace))
        .recoverWith {
          case err: Throwable => PrefixDirectLookup500(Error(s"error while doing a lookup on prefix ${prefix}", err.getMessage))
        }
            // ----- End of unmanaged code area for action  Semantic_repositoryYaml.prefixDirectLookup
        }
        val countTriplesByPrefix = countTriplesByPrefixAction { (prefix: String) =>  
            // ----- Start of unmanaged code area for action  Semantic_repositoryYaml.countTriplesByPrefix
            val namespace = kbrepo.prefixes.list().get(prefix)
      val result = kbrepo.store.size(namespace)

      Future.fromTry(result)
        .flatMap { triples => CountTriplesByPrefix200(TriplesCount(prefix, triples)) }
        .recoverWith {
          case err: Throwable =>
            val err_msg = s"cannot count triples for prefix ${prefix}"
            CountTriplesByPrefix500(Error(err_msg, s"${err}"))
        }
            // ----- End of unmanaged code area for action  Semantic_repositoryYaml.countTriplesByPrefix
        }
        val addOntology = addOntologyAction { input: (String, VocabulariesPostMime, VocabulariesPostMime, File, String) =>
            val (context, prefix, mime, rdfDocument, ontologyID) = input
            // ----- Start of unmanaged code area for action  Semantic_repositoryYaml.addOntology
            val _context = URLDecoder.decode(context, "UTF-8")
      val _mime = if (mime.get != null) URLDecoder.decode(mime.get, "UTF-8") else null
      val _prefix = prefix.getOrElse(null)

      val rdfFileName = rdfDocument.getName // CHECK: how to retrieve the original file name??
      val rdfURI = rdfDocument.toURI().toURL()

      logger.debug("\nADD ONTOLOGY......................................")
      logger.debug(ontologyID, _context, _prefix, _mime, rdfURI)

      val result = kbrepo.catalog.addOntology(rdfURI, _mime, ontologyID, _prefix, _context, _context)
      Future.fromTry(result)
        .flatMap { item =>
          val msg = s"""the document ${rdfFileName} was correctly added to context ${_prefix}:<${_context}>"""
          AddOntology200(ResultMessage(msg))
        }
        .recoverWith {
          case err: Throwable =>
            val msg = s"""the document ${rdfDocument.getAbsoluteFile} cannot be added to context ${_prefix}:<${_context}>"""
            AddOntology500(Error(msg, s"${err}"))
        }
            // ----- End of unmanaged code area for action  Semantic_repositoryYaml.addOntology
        }
        val removeOntologyByContext = removeOntologyByContextAction { (context: String) =>  
            // ----- Start of unmanaged code area for action  Semantic_repositoryYaml.removeOntologyByContext
            val _context = URLDecoder.decode(context, "UTF-8")
      val result = kbrepo.catalog.removeOntologyByURI(_context)

      logger.debug(s"removing ontology in context ${_context}")

      Future.fromTry(result)
        .flatMap { item =>
          val msg = s"""the ontology in <${_context}> was correctly deleted"""
          RemoveOntologyByContext200(ResultMessage(msg))
        }
        .recoverWith {
          case err: Throwable =>
            val msg = s"""cannot delete the ontology in context <${_context}>"""
            RemoveOntologyByContext500(Error(msg, s"${err}"))
        }
            // ----- End of unmanaged code area for action  Semantic_repositoryYaml.removeOntologyByContext
        }
        val prefixReverseLookup = prefixReverseLookupAction { (namespace: String) =>  
            // ----- Start of unmanaged code area for action  Semantic_repositoryYaml.prefixReverseLookup
            val prefixes = kbrepo.prefixes.list().get.map(item => (item._2, item._1))
      val _prefix = prefixes.get(namespace).get
      PrefixReverseLookup200(Prefix(_prefix, namespace)) // FIX encode/decode!
            // ----- End of unmanaged code area for action  Semantic_repositoryYaml.prefixReverseLookup
        }
        val contextsList = contextsListAction {  _ =>  
            // ----- Start of unmanaged code area for action  Semantic_repositoryYaml.contextsList
            val _contexts = kbrepo.store.contexts().get
        .map { cx => Context(cx, kbrepo.store.size(cx).get) }

      ContextsList200(_contexts)
        .recoverWith {
          case ex: Throwable => ContextsList500(Error(s"cannot retrieve contexts list!", ex.getMessage))
        }
            // ----- End of unmanaged code area for action  Semantic_repositoryYaml.contextsList
        }
        val addVocabulary = addVocabularyAction { input: (String, File, VocabulariesPostMime, String) =>
            val (vocabularyID, rdfDocument, mime, context) = input
            // ----- Start of unmanaged code area for action  Semantic_repositoryYaml.addVocabulary
            val _context = URLDecoder.decode(context, "UTF-8")
      val _mime = if (mime.get != null) URLDecoder.decode(mime.get, "UTF-8") else null

      val rdfFileName = rdfDocument.getName // CHECK: how to retrieve the original file name??
      val rdfURI = rdfDocument.toURI().toURL()

      logger.debug("\nADD ONTOLOGY......................................")
      logger.debug(vocabularyID, _context, _mime, rdfURI)

      val result = kbrepo.catalog.addOntology(rdfURI, _mime, vocabularyID, _context, _context)
      Future.fromTry(result)
        .flatMap { item =>
          val msg = s"""the document ${rdfFileName} was correctly added to context <${_context}>"""
          AddVocabulary200(ResultMessage(msg))
        }
        .recoverWith {
          case err: Throwable =>
            val msg = s"""the document ${rdfDocument.getAbsoluteFile} cannot be added to context <${_context}>"""
            AddVocabulary500(Error(msg, s"${err}"))
        }
            // ----- End of unmanaged code area for action  Semantic_repositoryYaml.addVocabulary
        }
        val removeVocabularyByContext = removeVocabularyByContextAction { (context: String) =>  
            // ----- Start of unmanaged code area for action  Semantic_repositoryYaml.removeVocabularyByContext
            val _context = URLDecoder.decode(context, "UTF-8")
      val result = kbrepo.catalog.removeVocabularyByURI(_context)

      logger.debug(s"removing vocabulary in context ${_context}")

      Future.fromTry(result)
        .flatMap { item =>
          val msg = s"""the vocabulary in <${_context}> was correctly deleted"""
          RemoveVocabularyByContext200(ResultMessage(msg))
        }
        .recoverWith {
          case err: Throwable =>
            val msg = s"""cannot delete the vocabulary in context <${_context}>"""
            RemoveVocabularyByContext500(Error(msg, s"${err}"))
        }
            // ----- End of unmanaged code area for action  Semantic_repositoryYaml.removeVocabularyByContext
        }
    
    }
}
