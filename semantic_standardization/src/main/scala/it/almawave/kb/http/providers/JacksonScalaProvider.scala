package it.almawave.kb.http.providers

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import com.fasterxml.jackson.databind.ObjectMapper
import javax.ws.rs.ext.Provider
import javax.ws.rs.Produces
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import javax.ws.rs.core.MediaType
import com.fasterxml.jackson.annotation.JsonInclude

import com.fasterxml.jackson.annotation.JsonAnyGetter

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.DeserializationFeature
import javax.ws.rs.ext.ContextResolver
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import java.lang.Double
import java.lang.Boolean

@Provider
@Produces(Array(MediaType.APPLICATION_JSON))
class JacksonScalaProvider extends JacksonJaxbJsonProvider with ContextResolver[ObjectMapper] {

  println("\n\nregistered " + this.getClass)

  val mapper = new ObjectMapper()

  mapper
    .registerModule(DefaultScalaModule)
    .setSerializationInclusion(JsonInclude.Include.ALWAYS)

    .configure(SerializationFeature.INDENT_OUTPUT, true)
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)

    .configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true)
    .configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true)
    .configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true)

    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
    .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
    .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
    //    .setVisibility(JsonMethod.FIELD, Visibility.ANY);

    .getSerializerProvider.setNullValueSerializer(new JsonSerializer[Object] {
      def serialize(obj: Object, gen: JsonGenerator, provider: SerializerProvider) {
        obj match {
          case bool: Boolean   => gen.writeBoolean(false)
          case number: Integer => gen.writeNumber(0)
          case number: Double  => gen.writeNumber(0.0D)
          case text: String    => gen.writeString("")
          case _               => gen.writeString("")
        }
      }
    })

  super.setMapper(mapper)

  override def getContext(klasses: Class[_]): ObjectMapper = mapper

}