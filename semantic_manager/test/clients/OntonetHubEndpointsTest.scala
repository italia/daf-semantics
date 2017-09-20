package clients

import scala.concurrent.Await

import org.junit.After
import org.junit.Before
import org.junit.Test

//import clients.OntonetHubClient
import play.api.libs.json.JsLookupResult
import play.api.libs.json.JsValue
import scala.concurrent.duration.Duration

class OntonetHubEndpointsTest {

  var http = HTTPClient
  var hub: OntonetHubClient = null

  @Before
  def before() {
    http.start()
    hub = new OntonetHubClient(http.ws)
  }

  @After
  def after() {
    http.stop()
  }

  @Test
  def test_02 {

    implicit def toList(root: JsLookupResult): List[JsValue] = root.as[List[JsValue]]

    val (query, lang, limit) = ("nam*", "", 4)

    val future = hub.find_property(query, lang, limit)

    val results = Await.result(future, Duration.Inf)
    println(results.mkString("\n"))
  }

}

