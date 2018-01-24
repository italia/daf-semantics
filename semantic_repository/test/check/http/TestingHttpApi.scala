package it.almawave.linkeddata.kb.http

import play.api.inject.guice.GuiceApplicationBuilder
import org.junit.Test
import org.junit.After
import play.api.Application
import org.junit.Before
import it.almawave.linkeddata.kb.utils.JSONHelper
import play.api.libs.ws.WSClient
import org.asynchttpclient.DefaultAsyncHttpClient
import play.api.libs.ws.ssl.SystemConfiguration
import akka.stream.ActorMaterializer
import play.api.libs.ws.ahc.AhcWSClient
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.net.URL
import com.typesafe.config.ConfigFactory

class TestingHttpApi {

  var app: Application = null
  var conf = ConfigFactory.empty()
  var ws: WSClient = null
  var app_url = new URL("http://localhost:8080")

  @Test
  def testing_contexts() {

    //    curl -X GET http://localhost:8999/kb/v1/prefixes/lookup?prefix=no_pref 
    //    -H  "accept: application/json" 
    //    -H  "content-type: application/json"

    val fut = ws.url(s"http://localhost:8999/kb/v1/prefixes/lookup")
      .withHeaders(("accept", "application/json"))
      .withHeaders(("content-type", "application/json"))
      .withFollowRedirects(true)
      .withQueryString(("prefix", "muapit"))
      .get()

    val results = Await.result(fut, Duration.Inf)
    println(results.body)

  }

  @Before
  def before() {

    app = GuiceApplicationBuilder()
      .build()

    conf = app.configuration.underlying

    // play.app.local.url
    // play.server.http.address
    // play.server.http.port

    println(JSONHelper.writeToString(conf.root().unwrapped()))

    app_url = new URL(conf.getString("app.local.url"))

    println(s"\n\nrunning at ${app_url}")

    val materializer = ActorMaterializer()(app.actorSystem)
    ws = AhcWSClient()(materializer)

  }

  @After
  def after() {
    ws.close()
    app.stop()
  }

}

