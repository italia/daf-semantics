package it.almawave.kb.http

import java.net.InetSocketAddress

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.glassfish.jersey.jackson.JacksonFeature
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.servlet.ServletContainer
import org.slf4j.LoggerFactory

import io.swagger.jaxrs.listing.ApiListingResource
import io.swagger.jaxrs.listing.SwaggerSerializers
import io.swagger.jersey.config.JerseyJaxrsConfig
import it.almawave.kb.http.providers.CORSFilter
import it.almawave.kb.http.providers.JacksonScalaProvider
import java.net.InetAddress
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.net.URL
import org.eclipse.jetty.server.handler.StatisticsHandler
import org.eclipse.jetty.server.handler.ShutdownHandler

object HTTP {

  def apply(conf: Config) = {

    val DEFAULT = ConfigFactory.parseString("""
    http {
    	host: 0.0.0.0
    	base: "/api"
    	port: 8080
    }  
    """)
    val _conf = conf.withFallback(DEFAULT).resolve()

    val host = _conf.getString("http.host")
    val port = _conf.getInt("http.port")
    val base = _conf.getString("http.base")
    new HTTP(host, port, base)
  }

  def apply(host: String, port: Int, base: String) = new HTTP(host, port, base)

}

/**
 * this is an object designed to simplify the creation of JAX-RS endpoints, using jetty
 * TODO: refactoring using conf?
 */
class HTTP(host: String, port: Int, base: String) {

  //  SEE ALSO: -Djava.net.preferIPv4Stack=true

  private val logger = LoggerFactory.getLogger(this.getClass)

  val address = parse_address(host, port)

  val server = new Server(address)

  //  import io.swagger.jaxrs2.integration.resources

  val resource_config = new ResourceConfig()
    .packages("io.swagger.jaxrs.listing")
    .packages("io.swagger.jaxrs2.integration.resources")
    .packages("it.almawave.kb.http.endpoints")
    .packages("it.almawave.kb.http.providers")
    .packages("it.almawave.kb.catalog.models")
    .registerClasses(
      classOf[ApiListingResource],
      classOf[SwaggerSerializers],
      classOf[JacksonFeature],
      classOf[JacksonScalaProvider],
      classOf[CORSFilter])

  logger.info("\n\n#### HTTP START with configuration:")
  logger.info(s"http://${host}:${port}${base}")

  // jersey configuration
  val jersey_container = new ServletContainer(resource_config)
  val jersey_holder = new ServletHolder(jersey_container);
  jersey_holder.setInitOrder(0)
  val jersey_context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS)
  jersey_context.setInitParameter("openApi.configuration.location", "conf/openapi-configuration.yaml")
  jersey_context.setContextPath(base)
  jersey_context.addServlet(jersey_holder, "/*")

  // swagger configuration
  val swagger_servlet_config = new JerseyJaxrsConfig
  val swagger_holder = new ServletHolder(jersey_container);

  swagger_holder.setInitParameter("swagger.api.title", "daf-standardization") // CHECK
  swagger_holder.setInitParameter("api.version", "0.0.4")
  swagger_holder.setInitParameter("info.description", """
    daf-standardization: a microservice exposing API supporting the DAF standardization process
  """) // CHECK
  swagger_holder.setInitParameter("swagger.api.basepath", s"http://localhost:${port}${base}")

  // CHECK: how to configure th basic settings from static file with overrides
  swagger_holder.setInitOrder(0)
  swagger_holder.setServlet(swagger_servlet_config)
  // la servlet è caricata ma non mappata su un path!
  jersey_context.addServlet(swagger_holder, null)

  // swagger ui
  val swagger_ui_handler = new ResourceHandler()
  swagger_ui_handler.setResourceBase("src/main/swagger-ui")
  val swagger_ui_context = new ContextHandler()
  swagger_ui_context.setContextPath("/")
  swagger_ui_context.setResourceBase(".")
  swagger_ui_context.setHandler(swagger_ui_handler)

  // CHECK: prometheus statistics ?
  // SEE: https://github.com/prometheus/client_java
  //  val stats = new StatisticsHandler()
  //  stats.setHandler(server.getHandler())
  //  server.setHandler(stats)
  //  // Register collector.
  //  new JettyStatisticsCollector(stats).register()

  val shutdown_handler = new ShutdownHandler(server, "kill_api")
  //  CHECK: "http://localhost:7777/shutdown?token=kill_api"

  val handlers = new HandlerList()
  handlers.setHandlers(Array(
    jersey_context,
    //    swagger_context,
    swagger_ui_context,
    shutdown_handler,
    new DefaultHandler()))

  server.setHandler(handlers)

  def start {
    logger.info(s"#### HTTP - START")
    server.start()
    server.join()
  }

  def stop {
    logger.info(s"#### HTTP - STOP")
    server.stop()
  }

  private def parse_address(host: String, port: Int): InetSocketAddress = {

    val _host = if (host != null)
      // TODO: handling this by URL instead of this hack!
      if (host.startsWith("http")) host.replaceAll("http://", "").replaceAll("https://", "") // hack
      else host
    else
      InetAddress.getLoopbackAddress.getHostName

    // SEE: InetAddress.getLocalHost.getCanonicalHostName

    // TESTING: if a different host is provided...
    new InetSocketAddress(_host, port)

  }

}