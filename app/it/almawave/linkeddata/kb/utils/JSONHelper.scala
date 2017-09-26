package it.almawave.linkeddata.kb.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object JSONHelper {

  private val json_mapper = new ObjectMapper().registerModule(DefaultScalaModule)
  private val json_writer = json_mapper.writerWithDefaultPrettyPrinter()
  private val json_reader = json_mapper.reader()

  def parse(json: String): JsonNode = {
    val content = json.replaceAll("(,)\\s+]", "]") // hack for removing trailing commas (invalid JSON)
    json_reader.readTree(content)
  }

  def writeToString(tree: Any): String = {
    json_writer.writeValueAsString(tree)
  }

  def pretty(json: String): String = {
    val tree = json_reader.readTree(json)
    json_writer.writeValueAsString(tree)
  }

}