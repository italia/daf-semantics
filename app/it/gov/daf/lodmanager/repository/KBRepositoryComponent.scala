package it.gov.daf.lodmanager.repository

import scala.concurrent.Future
import com.typesafe.config.Config

trait KBRepository {
  def getOntology(datasetId: String): Future[String]
}

trait KBRepositoryComponent {
  val kbRepository: KBRepository
}

// SPI factory!
object KBRepository {

  def apply(config: Config, app_mode: String): KBRepository = {

    app_mode match {
      case "dev" => new KBRepositoryDev(config)
      case _ => throw new RuntimeException("NOT IMPLEMENTED")
    }

  }

}