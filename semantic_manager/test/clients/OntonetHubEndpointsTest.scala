package clients

import scala.concurrent.Await

import org.junit.After
import org.junit.Before
import org.junit.Test

import play.api.libs.json.JsLookupResult
import play.api.libs.json.JsValue
import scala.concurrent.duration.Duration

import scala.concurrent.ExecutionContext.Implicits._
import utilities.JSONHelper
import org.junit.Assert
import semantic_manager.yaml.OntonetHubProperty
import clients.HTTPClient

class OntonetHubEndpointsTest {

  var http = HTTPClient
  var hub: OntonetHubClient = null

  @Before
  def before() {
    http.start()
    hub = new OntonetHubClient(http.ws, OntonetHubClient.DEFAULT_CONFIG)
  }

  @After
  def after() {
    http.stop()
  }

  //  @Test
  def testing_hub_find {

    val (host, port) = ("localhost", 8000)
    val (query, lang, limit) = ("nome", "it", 4)

    val http = HTTPClient
    http.start()

    val ws = http.ws

    val future = ws.url(s"http://${host}:${port}/stanbol/ontonethub/ontologies/find")
      .withHeaders(("accept", "application/json"))
      .withHeaders(("content-type", "application/x-www-form-urlencoded"))
      .withFollowRedirects(true)
      .post(s"name=${query}&lang=${lang}&limit=${limit}")
      .map { item =>
        val json = JSONHelper.pretty(item.body)
        println("\n\n")
        println(json)
        item
      }

    val results = Await.result(future, Duration.Inf)
    Assert.assertTrue(results.status == 200)

    http.stop()

  }

  @Test
  def testing_find_property {

    val (query, lang, limit) = ("nome", "it", 2)

    val future = hub.find_property(query, lang, limit)

    // CHECK for de-coupling from swagger
    //      .map(_.map(item => OntonetHubProperty.tupled(OntonetHubClient.models.FindResult.unapply(item).get)))

    println("\n\n############################################ RESULTS")
    val results = Await.result(future, Duration.Inf)
    println(results.mkString("\n\n"))

  }

}

