package utilities

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.JsonNode

object JSONHelper {

  private val json_mapper = new ObjectMapper
  private val json_writer = json_mapper.writerWithDefaultPrettyPrinter()
  private val json_reader = json_mapper.reader()

  def read(json: String): JsonNode = {
    val content = json.replaceAll("(,)\\s+]", "]") // hack for removing trailing commas (invalid JSON)
    json_reader.readTree(content)
  }

  def write(json_tree: JsonNode): String = {
    json_writer.writeValueAsString(json_tree)
  }

  def pretty(json: String): String = {
    val tree = read(json)
    write(tree)
  }

}