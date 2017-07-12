package it.gov.daf.lodmanager.service.disabled

import it.gov.daf.lodmanager.repository.KBRepositoryComponent
import scala.concurrent.Future

trait KBServiceComponent {

  this: KBRepositoryComponent => val kbService: KBService

  //  import scala.concurrent.ExecutionContext.Implicits.global

  class KBService {

    def getOntology(name: String): Future[String] = {
      kbRepository.getOntology(name)
    }

  }
}