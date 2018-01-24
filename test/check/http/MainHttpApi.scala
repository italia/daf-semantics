package check.http

import play.api.inject.guice.GuiceApplicationBuilder
import java.io.File

object MainHttpApi extends App {

  val app = GuiceApplicationBuilder()
    .in(new File("conf/application.conf"))
    .build()

  //  println(JSONHelper.writeToString(app.configuration.underlying.root().unwrapped()))

  //  app.stop()
}