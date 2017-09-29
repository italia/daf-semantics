package modules

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.google.inject.ImplementedBy

import it.gov.daf.semantics.api.OntologyAPIFactory
import it.gov.daf.semantics.api.VocabularyAPIFactory
import javax.inject.Inject
import javax.inject.Singleton
import play.api.Application
import play.api.Configuration
import play.api.Environment
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import it.almawave.linkeddata.kb.repo.RDFRepository
import play.Mode
import com.typesafe.config.Config
import com.typesafe.config.ConfigValueFactory
import it.almawave.linkeddata.kb.repo.utils.ConfigHelper

@ImplementedBy(classOf[KBModuleBase])
trait KBModule

@Singleton
class KBModuleBase @Inject() (lifecycle: ApplicationLifecycle) extends KBModule {

  // TODO: SPI per dev / prod
  val kbrepo = RDFRepository.memory()

  // OntologyAPI service
  val ontologyAPI = new OntologyAPIFactory()

  // VocabularyAPI service
  val vocabularyAPI = new VocabularyAPIFactory()

  // when application starts...
  @Inject
  def onStart(
    app: Application,
    env: Environment,
    configuration: Configuration)(implicit ec: ExecutionContext) {

    Logger.info("KBModule.START....")

    // get configs
    val app_type = configuration.underlying.getString("app.type")
    val data_dir = app_type match {
      case "dev"  => "./dist/data"
      case "prod" => "./data"
    }

    println("\n\n\nUSING DATA DIR:" + data_dir)

    // starting OntologyAPI service
    //    val config_ontologies = ConfigHelper.injectParameter(OntologyAPIFactory.DEFAULT_CONFIG, "data_dir", data_dir)
    val config_ontologies = ConfigHelper.injectParameter(OntologyAPIFactory.DEFAULT_CONFIG, "data_dir", "./dist/data")
    ontologyAPI.config(config_ontologies)
    ontologyAPI.start()

    // starting VocabularyAPI service
    //    val config_vocabularies = ConfigHelper.injectParameter(VocabularyAPIFactory.DEFAULT_CONFIG, "data_dir", data_dir)
    val config_vocabularies = ConfigHelper.injectParameter(VocabularyAPIFactory.DEFAULT_CONFIG, "data_dir", "./dist/data")
    vocabularyAPI.config(config_vocabularies)
    vocabularyAPI.start()

  }

  // when application stops...
  lifecycle.addStopHook({ () =>

    Future.successful {

      // stopping VocabularyAPI service
      vocabularyAPI.stop()

      // stopping OntologyAPI service
      ontologyAPI.stop()

      // this is useful for saving files, closing connections, release indexes, etc
      //      kbrepo.stop()
      Logger.info("KBModule.STOP....")

    }

  })

}

