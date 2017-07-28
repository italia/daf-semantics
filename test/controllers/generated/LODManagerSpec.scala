//package controllers.generated
//
//import java.io.IOException
//import java.net.ServerSocket
//
//import akka.actor.ActorSystem
//import akka.stream.ActorMaterializer
//import org.specs2.mutable.Specification
//import play.api.Application
//import play.api.http.Status
//import play.api.routing.Router
//import play.api.inject.guice.GuiceApplicationBuilder
//import play.api.libs.json.{ JsArray, JsValue, Json }
//import play.api.libs.ws.WSResponse
//import play.api.libs.ws.ahc.AhcWSClient
//import play.api.test._
//
//import scala.concurrent.duration.Duration
//import scala.concurrent.{ Await, Future }
//
//import lod_manager.yaml.Error
//import org.junit.runner.RunWith
//
//import org.scalatest.Specs
//
//class LODManagerSpec extends Specification {
//
//  def application: Application = GuiceApplicationBuilder().build()
//
//  "The lod-manager" should {
//
//    "Call kb/v1/contexts return ok status" in
//      new WithServer(app = application, port = 9000) {
//        WsTestClient.withClient { implicit client =>
//          val response: WSResponse = Await.result[WSResponse](
//            client
//              .url(s"http://localhost:9000/catalog-manager/v1/dataset-catalogs").
//              execute, Duration.Inf)
//          println(response.status)
//          response.status must be equalTo Status.OK
//        }
//      }
//
//  }
//
//}