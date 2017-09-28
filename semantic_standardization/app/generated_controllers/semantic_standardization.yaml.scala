
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
import play.api.libs.ws.WSClient
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * This controller is re-generated after each change in the specification.
 * Please only place your hand-written code between appropriate comments in the body of the controller.
 */

package semantic_standardization.yaml {
    // ----- Start of unmanaged code area for package Semantic_standardizationYaml
    
    // ----- End of unmanaged code area for package Semantic_standardizationYaml
    class Semantic_standardizationYaml @Inject() (
        // ----- Start of unmanaged code area for injections Semantic_standardizationYaml

      kb: KBModuleBase,
      ws: WSClient,

        // ----- End of unmanaged code area for injections Semantic_standardizationYaml
        val messagesApi: MessagesApi,
        lifecycle: ApplicationLifecycle,
        config: ConfigurationProvider
    ) extends Semantic_standardizationYamlBase {
        // ----- Start of unmanaged code area for constructor Semantic_standardizationYaml

    // wrapper for triplestore
    val kbrepo = kb.kbrepo

        // ----- End of unmanaged code area for constructor Semantic_standardizationYaml
        val propertiesHierarchyList = propertiesHierarchyListAction { input: (String, String, String) =>
            val (vocabulary_name, ontology_name, lang) = input
            // ----- Start of unmanaged code area for action  Semantic_standardizationYaml.propertiesHierarchyList
            val parameters = Map("lang" -> lang)
      val vocapi = kb.vocabularyAPI.items(vocabulary_name)
      val ontoapi = kb.ontologyAPI.items(ontology_name)

      val fields = vocapi.extract_keys(parameters)
      val items = ontoapi.extract_hierarchy_properties(parameters, fields)
        .map { item =>
          val vocabulary = item.get("vocabulary").get.asInstanceOf[String]
          val path = item.get("path").get.asInstanceOf[String]
          val hierarchy_flat = item.get("hierarchy_flat").get.asInstanceOf[String]
          val hierarchy = item.get("hierarchy").get.asInstanceOf[List[Map[String, Object]]]
            .map { el =>
              val klass = el.get("class").get.asInstanceOf[String]
              val level = BigInt.int2bigInt(el.get("level").get.asInstanceOf[Integer])
              //              val level = el.get("level").get.asInstanceOf[Integer]
              PropertyHierarchyLevel(klass, level)
            }
          PropertyHierarchy(vocabulary, path, hierarchy_flat, "hierarchy")
        }

      PropertiesHierarchyList200(items)
        .recoverWith { case ex: Throwable => PropertiesHierarchyList500(Error(s"cannot obtain property hierarchies for ${ontology_name}/${vocabulary_name} ", ex.getMessage)) }
            // ----- End of unmanaged code area for action  Semantic_standardizationYaml.propertiesHierarchyList
        }
        val vocabularyDataset = vocabularyDatasetAction { input: (String, String) =>
            val (name, lang) = input
            // ----- Start of unmanaged code area for action  Semantic_standardizationYaml.vocabularyDataset
            val vocapi = kb.vocabularyAPI.items(name)
      val tree = vocapi.extract_data(Map("lang" -> "it"))

      val items = tree
        .map {
          _.toList.map { item =>
            VocabularyItemValue(item._1, item._2.toString())
          }.toSeq
        }.toSeq

      VocabularyDataset200(items)
        .recoverWith {
          case ex: Throwable =>
            VocabularyDataset500(Error(s"problems obtaining a flat representation for Vocabulary ${name}", ex.getMessage))
        }
            // ----- End of unmanaged code area for action  Semantic_standardizationYaml.vocabularyDataset
        }
    
    }
}
