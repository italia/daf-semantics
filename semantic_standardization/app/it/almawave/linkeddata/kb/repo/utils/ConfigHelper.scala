package it.almawave.linkeddata.kb.repo.utils

import com.typesafe.config.Config
import com.typesafe.config.ConfigValueFactory

object ConfigHelper {

  def injectParameter(conf: Config, conf_key: String, conf_value: Object) = {
    conf
      .withValue(conf_key, ConfigValueFactory.fromAnyRef(conf_value))
      .resolve()
      .withoutPath(conf_key)
  }

}