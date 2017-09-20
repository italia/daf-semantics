package review.repository

import play.api.libs.ws.WSClient
import scala.concurrent.Future
import akka.stream.scaladsl.Source
import play.api.mvc.MultipartFormData.DataPart
import play.api.mvc.MultipartFormData.FilePart
import akka.stream.scaladsl.FileIO
import java.io.File

object SemanticRepositoryClient {

  def create(implicit ws: WSClient) = new SemanticRepositoryClient(ws)

}

class SemanticRepositoryClient(ws: WSClient) {

  import scala.concurrent.ExecutionContext.Implicits._

  // TODO: export the configurations
  val host = "localhost"
  val port = 8888
  val FOLLOW_REDIRECTS = true
  val PAUSE = 1000

  /**
   * basic crud functions
   */
  object crud {

    def add_ontology(fileName: String, rdfDocument: File, prefix: String, context: String): Future[String] = {

      val rdfPath = rdfDocument.toPath().toAbsolutePath()

      // val fileMime = "text/turtle" // DISABLED
      val add_src = Source(List(
        DataPart("fileName", fileName),
        DataPart("prefix", prefix),
        DataPart("context", context),
        FilePart("data", fileName, None, FileIO.fromPath(rdfPath))))

      ws.url(urls.ontologies_list)
        .withHeaders(("Accept", "application/json"))
        .withFollowRedirects(FOLLOW_REDIRECTS)
        .post(add_src)
        .map { response => response.body }
        .map { response => "OK!" } // test
        .recover {
          case ex: Throwable => s"problems adding document ${fileName}\n${ex}"
        }

    }

    def remove(context: String): Future[String] = {

      ws.url(urls.ontologies_list)
        .withFollowRedirects(FOLLOW_REDIRECTS)
        .post(s"context=${context}")
        .map { response => response.body }

    }

  }

  object lookup {

    def contexts(): Future[String] = {
      ws.url(urls.contexts)
        .withFollowRedirects(FOLLOW_REDIRECTS)
        .get()
        .map { response => response.body } // REVIEW: response model
    }

    /* TODO
    def prefixes(): Future[String] = {
      ws.url(urls.prefixes)
        .withFollowRedirects(FOLLOW_REDIRECTS)
        .get()
        .map { response => response.body } // REVIEW: response model
    }
    
    def prefixes_lookup(): Future[String] = {
      ws.url(urls.prefixes_lookup)
        .withFollowRedirects(FOLLOW_REDIRECTS)
        .get()
        .map { response => response.body } // REVIEW: response model
    }
    */

  }

  object urls {

    def ontologies_list = s"http://${host}:${port}/kb/v1/ontologies"
    def ontology_delete(context: String) = s"http://${host}:${port}/kb/v1/ontologies/remove"

    def contexts = s"http://${host}:${port}/kb/v1/contexts"

    def prefixes = s"http://${host}:${port}/kb/v1/prefixes"
    def prefixes_lookup = s"http://${host}:${port}/kb/v1/prefixes/lookup"
    def prefixes_reverse = s"http://${host}:${port}/kb/v1/prefixes/reverse"

    def triples = s"http://${host}:${port}/kb/v1/triples"
    def triples_by_prefix(prefix: String) = s"http://${host}:${port}/kb/v1/triples/${prefix}"

  }

}