package it.almawave.kb.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.JsonNode

object JSONHelper {

  private val json_mapper = new ObjectMapper
  private val json_writer = json_mapper.writerWithDefaultPrettyPrinter()
  private val json_reader = json_mapper.reader()

  def read(json: String): JsonNode = json_reader.readTree(json)

  def pretty(json: String): String = {
    val tree = json_reader.readTree(json)
    json_writer.writeValueAsString(tree)
  }

}