package it.almawave.kb.http.providers

import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider
import javax.ws.rs.core.Response
import com.sun.media.sound.InvalidDataException

@Provider
class NotImplementedExceptionHandler extends ExceptionMapper[Exception] {

  override def toResponse(err: Exception): Response = {
    Response
      .status(Response.Status.NOT_IMPLEMENTED)
      .entity("not implemented yet")
      .build()
  }

}
