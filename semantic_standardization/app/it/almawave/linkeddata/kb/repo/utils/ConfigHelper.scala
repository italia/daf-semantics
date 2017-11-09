package it.almawave.linkeddata.kb.repo.utils

import com.typesafe.config.Config
import com.typesafe.config.ConfigValueFactory
import com.typesafe.config.ConfigFactory
import java.io.File

object ConfigHelper {

  def injectParameter(conf: Config, conf_key: String, conf_value: Object) = {
    conf
      .withValue(conf_key, ConfigValueFactory.fromAnyRef(conf_value))
      .resolve()
      .withoutPath(conf_key)
  }

  def load(conf_files: String*): Config = {

    var conf = ConfigFactory.empty()

    conf_files.foreach { conf_name =>
      val file = new File(conf_name).getAbsoluteFile
      conf = conf.withFallback(ConfigFactory.parseFile(file))
    }

    conf.resolve()

  }

}