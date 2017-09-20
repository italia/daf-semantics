package specs

import org.junit.runner.RunWith

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.Duration

import play.api.test._
import play.api.http.Status
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSResponse
import play.api.libs.ws.ahc.AhcWSClient
import org.specs2.runner.JUnitRunner
//import org.specs2.Specification
import org.specs2.mutable.Specification
import play.api.libs.json.Json

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
//import play.api.libs.json.JsObject
import play.twirl.api.Content
import play.api.test.Helpers._
import play.api.libs.json.JsObject
import java.io.File
import play.api.http.Writeable
import akka.stream.scaladsl.Source
//import play.mvc.Http$MultipartFormData.Part
//import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.MultipartFormData
import play.api.libs.Files.TemporaryFile
import java.nio.file.Files
import org.asynchttpclient.AsyncHttpClient
import play.api.libs.ws.WS
import akka.util.ByteString
import play.api.mvc.MultipartFormData.DataPart
import play.api.mvc.MultipartFormData.FilePart
import akka.stream.scaladsl.FileIO
import play.api.libs.ws.WSClient

@RunWith(classOf[JUnitRunner])
class LODManagerSpec extends Specification {

  def application: Application = GuiceApplicationBuilder().build()

  val host = "localhost"
  val port = 8000

  "The OntonetHub" should {

    "ontonethub: list all the ontologies" in {

      WsTestClient.withClient { implicit client =>

        val response: WSResponse = Await.result[WSResponse](
          client.url(s"http://${host}:${port}/stanbol/ontonethub/ontologies")
            .get(),
          Duration.Inf)

        println("\n\n\n\nCHECK: ontonethub - list of ontologies")
        println(response.json)

        action(response)
      }

    }

    //    "add rdf document" in {
    //      new WithServer(app = application, port = 9999) {
    //        WsTestClient.withClient { implicit client =>
    //
    //          //          implicit val wrt: Writeable[Source[_ <: Part[Any], Any]] = null
    //
    //          // ESEMPIO DANIELE
    //          //          val file = Environment.simple().getFile("test/specs/test.ttl").toPath()
    //          //          val fp = FilePart("rdfDocument", "test.ttl", Option("text/turtle"), FileIO.fromPath(file))
    //          //          val dp = List(DataPart("name", "test.ttl"), DataPart("validator", "1"))
    //          //          val source = Source(fp :: dp)
    //          //          val response: WSResponse = Await.result[WSResponse](client.url(s"http://localhost:${port}/validator/validate").post(source), Duration.Inf)
    //
    //          val body: Source[MultipartFormData.Part[Source[ByteString, _]], _] =
    //            Source(
    //              List(
    //                DataPart("uno", "11"), DataPart("due", "22"),
    //                FilePart("", "", Option("text/turtle"), FileIO.fromFile(new File("ok")))))
    //
    //          val response: WSResponse = Await.result[WSResponse](
    //            client.url(s"http://localhost:${port}/vb/v1/ontologies")
    //              //            .def post(body: Source[MultipartFormData.Part[Source[ByteString, _]], _]): Future[WSResponse]
    //              .post(body),
    //            //              .withMethod("POST")
    //            //               .withBody(new File(""))
    //            // .withQueryString(params: _*)
    //            //              .execute(),
    //            Duration.Inf)
    //
    //          action(response)
    //
    //        }
    //      }
    //    }

  }

}