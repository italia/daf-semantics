package it.gov.daf.lodmanager.repository

import play.api.libs.json._

import scala.concurrent.Future
import javax.inject.Inject
import com.typesafe.config.Config
import it.gov.daf.lodmanager.utility.ConfigHelper

class KBRepositoryDev(conf: Config) extends KBRepository {

  // TODO
  import scala.concurrent.ExecutionContext.Implicits.global

  println("TEST::CONF:: " + ConfigHelper.pretty(conf.getConfig("ontologies")))

  override def getOntology(slug: String): Future[String] = Future {

    val config_rendered = ConfigHelper.pretty(conf.getConfig("ontologies." + slug))
    println("TEST::CONF:: " + config_rendered)

    config_rendered

  }

}