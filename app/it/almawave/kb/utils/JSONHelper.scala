package it.almawave.kb.utils

import com.fasterxml.jackson.databind.ObjectMapper

object JSONHelper {

  private val json_mapper = new ObjectMapper
  private val json_writer = json_mapper.writerWithDefaultPrettyPrinter()
  private val json_reader = json_mapper.reader()

  def pretty(json: String): String = {

    val tree = json_reader.readTree(json)
    json_writer.writeValueAsString(tree)

  }

}