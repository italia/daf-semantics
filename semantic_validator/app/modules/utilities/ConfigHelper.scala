package modules.utilities

import com.typesafe.config.Config
import com.typesafe.config.ConfigRenderOptions
import com.typesafe.config.ConfigValueFactory

object ConfigHelper {

  private val options_render = ConfigRenderOptions.concise()
    .setComments(false)
    .setOriginComments(false)
    .setFormatted(true)
    .setJson(false)

  def pretty(conf: Config) = conf.root().render(options_render)

  def injectParameters(conf: Config, params: (String, Object)*) = {
    var config = conf
    params.foreach { entry =>
      val conf_key = entry._1
      val conf_value = ConfigValueFactory.fromAnyRef(entry._2)
      val substitution = conf.withValue(conf_key, conf_value)
      config = substitution.withFallback(config)
    }
    config.resolve()
  }

}

