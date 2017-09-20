package specs

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import org.junit.runner.RunWith
import org.specs2.mutable.Specification

import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.JsObject
import play.api.libs.ws.WSResponse
import play.api.test.WithServer
import play.api.test.WsTestClient
import akka.stream.scaladsl.FileIO
import play.api.Environment

import org.specs2.runner.JUnitRunner
import akka.stream.scaladsl.Source
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc.MultipartFormData.DataPart
import play.api.mvc.MultipartFormData
import akka.util.ByteString

@RunWith(classOf[JUnitRunner])
class LODValidatorSpec extends Specification {

  println("##### executing LODValidatorSpec")

  def application: Application = GuiceApplicationBuilder().build()

  /**
   * Legge un documento da file
   *
   * private def readDocFile(path: String): String = {
   * val fdoc = Environment.simple().getFile(path)
   * var bufferedSource: BufferedSource = null
   * var res: String = ""
   * try {
   *
   * bufferedSource = Source.fromFile(fdoc)
   * for (line <- bufferedSource.getLines) {
   * res += (line + "\n")
   * }
   *
   * } catch {
   * case tr: Throwable => tr.printStackTrace
   * } finally {
   *
   * }
   * res
   * }
   *
   *
   *
   *
   * "The lod-validator" should {
   *
   * "call validator/validators  to obtain a list of validators" in {
   * new WithServer(app = application, port = 9999) {
   * WsTestClient.withClient { implicit client =>
   *
   * val response: WSResponse = Await.result[WSResponse](client.url(s"http://localhost:${port}/validator/validators").execute, Duration.Inf)
   *
   * response.status must be equalTo Status.OK
   * response.json.as[Seq[JsObject]].size must be > 0
   *
   * }
   * }
   * }
   *
   * }
   *
   */

  /**
   * TODO: non funziona il client multipart file + dati
   */

  "The lod-validator" should {

    "call validator/validate  to validate a document" in {
      new WithServer(app = application, port = 9999) {
        WsTestClient.withClient { implicit client =>

          val file = Environment.simple().getFile("test/specs/test.ttl").toPath()
          val fp = FilePart("rdfDocument", "test.ttl", Option("text/turtle"), FileIO.fromPath(file))
          val dp = List(DataPart("name", "test.ttl"), DataPart("validator", "1"))
          val source = Source(fp :: dp)
          val response: WSResponse = Await.result[WSResponse](client.url(s"http://localhost:${port}/validator/validate").post(source), Duration.Inf)

          response.status must be equalTo Status.OK

          //TODO: non è una sequence la risposta... modificare
          //response.json.as[Seq[JsObject]].size must be > 0

        }
      }
    }

  }

}