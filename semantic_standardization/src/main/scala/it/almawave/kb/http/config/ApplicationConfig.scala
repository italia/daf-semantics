package it.almawave.kb.http.config

import javax.ws.rs.ApplicationPath

import java.util.{ Map => JMap, HashMap => JHashMap, List => JList, HashSet => JHashSet }
import javax.ws.rs.core.Application
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.server.wadl.config.WadlGeneratorConfig
import io.swagger.jaxrs.listing.SwaggerSerializers
import io.swagger.jaxrs.listing._
import io.swagger.jersey.listing.ApiListingResourceJSON
import org.glassfish.jersey.jackson.JacksonFeature
import it.almawave.kb.http.providers.CORSFilter
import it.almawave.kb.http.providers.JacksonScalaProvider

@ApplicationPath("/kb/api")
class ApplicationConfig extends Application {

  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters._

  override def getClasses() = {

    val resources = new JHashSet[Class[_]]
    resources.add(classOf[ApiListingResource])
    resources.add(classOf[SwaggerSerializers])
    resources.add(classOf[ApiListingResourceJSON])

    resources.add(classOf[JacksonFeature])
    resources.add(classOf[JacksonScalaProvider])
    resources.add(classOf[CORSFilter])

    resources
  }

  override def getProperties(): JMap[String, Object] = {

    val properties = new JHashMap[String, Object]
    properties.put(ServerProperties.WADL_FEATURE_DISABLE, "false")
    properties.put(ServerProperties.APPLICATION_NAME, "katalod")

    properties.put(ServerProperties.PROVIDER_SCANNING_RECURSIVE, "true")
    properties.put(ServerProperties.PROVIDER_PACKAGES, Array(
      "it.almawave.kb.http.providers",
      "it.almawave.kb.http.endpoints" // "io.swagger.jaxrs.listing"
      ))
    // io.swagger.jaxrs.listing.ApiListingResource, io.swagger.jaxrs.listing.SwaggerSerializers,

    properties.put(ServerProperties.MONITORING_ENABLED, "true")
    properties.put(ServerProperties.MONITORING_STATISTICS_ENABLED, "true")

    properties
  }

}