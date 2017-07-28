package it.almawave.kb

import com.typesafe.config.Config
import com.typesafe.config.ConfigRenderOptions
import com.fasterxml.jackson.databind.ObjectMapper

object ConfigHelper {

  private val options_render = ConfigRenderOptions.concise()
    .setComments(false)
    .setOriginComments(false)
    .setFormatted(true)
    .setJson(false)

  //  private val conf: Config = ConfigFactory.empty()

  def pretty(conf: Config) = conf.root().render(options_render)

}

object JSONHelper {

  private val json_mapper = new ObjectMapper
  private val json_writer = json_mapper.writerWithDefaultPrettyPrinter()
  private val json_reader = json_mapper.reader()

  def pretty(json: String): String = {

    val tree = json_reader.readTree(json)
    json_writer.writeValueAsString(tree)

  }

}