package specs

import play.api.test.WsTestClient
import play.api.libs.ws.WSResponse
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import play.api.test.WithServer
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

trait TestingRequests {

  val host = "localhost"
  val port = 9999
  def application: Application = GuiceApplicationBuilder().build()

  def GET(path: String)(action: WSResponse => Any) = new WithServer(app = application, port = port) {
    WsTestClient.withClient { implicit client =>
      val response: WSResponse = Await.result[WSResponse](
        client.url(s"http://${host}:${port}${path}")
          .withMethod("GET")
          .execute(),
        Duration.Inf)
      action(response)
    }
  }

//  def CRUD(method: String = "POST")(path: String)(body: String, params: (String, String)*)(action: WSResponse => Any) = new WithServer(app = application, port = port) {
//    WsTestClient.withClient { implicit client =>
//      val response: WSResponse = Await.result[WSResponse](
//        client.url(s"http://${host}:${port}${path}")
////          .withMethod(method)
////          .withBody(body)
////          .withQueryString(params: _*)
//          .execute(),
//        Duration.Inf)
//      action(response)
//    }
//  }

}