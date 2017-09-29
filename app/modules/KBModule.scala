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

@ImplementedBy(classOf[KBModuleBase])
trait KBModule

@Singleton
class KBModuleBase @Inject() (lifecycle: ApplicationLifecycle) extends KBModule {

  // TODO: SPI per dev / prod
  val kbrepo = RDFRepository.memory()

  //  val repo: Repository = new SailRepository(new MemoryStore)

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

    val kbconf = configuration.getConfig("kb")
      .getOrElse(Configuration.empty)
      .underlying

    Logger.info("KBModule.START....")

    // starting OntologyAPI service
    ontologyAPI.start()

    // starting VocabularyAPI service
    vocabularyAPI.start()

  }

  // when application stops...
  lifecycle.addStopHook({ () =>

    Future.successful {

      // stopping VocabularyAPI service
      vocabularyAPI.start()

      // stopping OntologyAPI service
      ontologyAPI.stop()

      // this is useful for saving files, closing connections, release indexes, etc
      //      kbrepo.stop()
      Logger.info("KBModule.STOP....")

    }

  })

}

