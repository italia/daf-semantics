package it.almawave.kb.http.endpoints

import java.time.LocalTime
import io.swagger.annotations.Api
import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.Produces
import io.swagger.annotations.ApiOperation
import javax.ws.rs.core.MediaType
import org.slf4j.LoggerFactory
import javax.ws.rs.core.Context
import javax.ws.rs.core.UriInfo
import javax.ws.rs.core.Request
import it.almawave.linkeddata.kb.utils.JSONHelper
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.ZoneId

@Api(tags = Array("catalog"))
@Path("/status")
class Status {

  private val logger = LoggerFactory.getLogger(this.getClass)

  @Context
  var uriInfo: UriInfo = null

  @GET
  @Produces(Array(MediaType.APPLICATION_JSON))
  @ApiOperation(nickname = "status", value = "endpoint status")
  def status() = {

    val base_uri = uriInfo.getBaseUri
    val msg = s"the service is running at ${base_uri}"
    logger.info(msg)

    val _now = now()
    StatusMsg(_now._1, _now._2, msg)

  }

  def now() = {

    val zdt = ZonedDateTime.now(ZoneId.of("+1"))
    val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ")

    (zdt.format(dtf), zdt)

  }

}

case class StatusMsg(
  now:      String,
  dateTime: ZonedDateTime,
  msg:      String
)