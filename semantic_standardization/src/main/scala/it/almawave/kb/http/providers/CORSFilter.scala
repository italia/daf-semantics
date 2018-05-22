package it.almawave.kb.http.providers

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.ext.Provider
import java.text.SimpleDateFormat
import java.util.Date
import java.net.URI

@Provider
class CORSFilter extends ContainerResponseFilter {

  override def filter(request: ContainerRequestContext, response: ContainerResponseContext) {

    val headers = response.getHeaders()

    headers.add("Access-Control-Allow-Origin", "*")
    headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
    headers.add("Access-Control-Allow-Credentials", "true")
    headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")

    // custom headers
    //    headers.add("Server", "Simple Jersey/Jetty HTTP server for RDF")
    //    headers.add("Pragma", "Pragma: no-cache")
    //    headers.add("Link", new URI("http://almawave.it"))

  }

}