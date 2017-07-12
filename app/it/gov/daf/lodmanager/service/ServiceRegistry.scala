package it.gov.daf.lodmanager.service

import play.api.{ Configuration, Environment }
import it.gov.daf.lodmanager.repository.KBRepository
import it.gov.daf.lodmanager.repository.KBRepositoryComponent

// IDEA: object ServiceRegistry extends KBServiceComponent with KBRepositoryComponent {
object ServiceRegistry extends KBRepositoryComponent {

  // ASK: how to start / stop a service provider for data?
  
  val conf_play: Configuration = Configuration.load(Environment.simple())
  val app_mode: String = conf_play.getString("app.type").getOrElse("dev")

  val conf_kb = conf_play.getConfig("kb").get.underlying

  val kbRepository = KBRepository(conf_kb, app_mode)

}
