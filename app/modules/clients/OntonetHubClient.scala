package modules

import it.almawave.kb.utils.JSONHelper
import play.api.libs.ws.WSClient
import scala.concurrent.Future
import java.nio.file.Paths
import akka.stream.scaladsl.Source
import play.api.mvc.MultipartFormData.DataPart
import play.api.mvc.MultipartFormData.FilePart
import akka.stream.scaladsl.FileIO
import scala.util.Success
import scala.util.Failure
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import akka.dispatch.Futures

object OntonethubClient {

  def create(implicit ws: WSClient) = new OntonethubClient(ws)

}

// TODO: refactorization, after local testing
class OntonethubClient(ws: WSClient) {

  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters._
  import scala.concurrent.ExecutionContext.Implicits._

  val host = "localhost"
  val port = 8000
  val FOLLOW_REDIRECTS = true

  // this object encapsulates various types of lookup
  object lookup {

    // returns a list of ontology uris from ontonethub
    def list_ids: Future[List[String]] = {
      list_uris.map {
        list =>
          list.map { node =>
            val uri = node
            uri.substring(uri.lastIndexOf("/") + 1)
          }
      }
    }

    // returns a list of ontology ids
    def list_uris: Future[List[String]] = {
      ws.url(urls.ontologies_list)
        .withFollowRedirects(FOLLOW_REDIRECTS)
        .get()
        .map { response =>
          JSONHelper.read(response.body).toList
            .map { node =>
              node.asText()
            }
        }
    }

    // returns a list of ontology uris from ontonethub
    def find_id_by_prefix(prefix: String) = {
      Await.result(ids_for_prefixes, Duration.Inf).get(prefix)
    }

    // returns a list of (prefix, id) pair 
    private def ids_for_prefixes() = {

      val list = Await.result(list_ids, Duration.Inf)
      val futures = list.map { onto_id =>
        ws.url(urls.ontology_metadata(onto_id))
          .withFollowRedirects(FOLLOW_REDIRECTS)
          .get()
          .map { res => JSONHelper.read(res.body) }
          .map { json => (json.get("name").asText(), json.get("id").asText()) }
      }

      val ex = scala.concurrent.ExecutionContext.Implicits.global
      Futures.sequence(futures, ex)
        .map {
          ops => ops.toMap
        }

    }

    //    def find_ontology_by_prefix(prefix: String) = {
    //
    //      val params = Map("name" -> prefix)
    //        .map(item => s"${item._1}=${item._2}")
    //        .mkString("&")
    //
    //      ws.url(urls.find_ontologies)
    //        .withFollowRedirects(FOLLOW_REDIRECTS)
    //        .withHeaders(("accept", "application/json"))
    //        .withHeaders(("content-type", "application/x-www-form-urlencoded"))
    //        .post(params)
    //        .map { res => res.body }
    //
    //    }

  }

  // this object encapsulates the methods for CRUD operations
  object crud {

    // retrieves the content (source) of an ontology
    def get(ontologyID: String, mime: String = "text/turtle"): Future[String] = {

      ws.url(urls.ontology_source(ontologyID))
        .withFollowRedirects(FOLLOW_REDIRECTS)
        .withHeaders(("Accept", "text/turtle"))
        .get()
        .map { res => res.body }

    }

    def clear() {
      val ids: List[String] = Await.result(lookup.list_ids, Duration.Inf)
      ids.foreach { id =>
        Await.result(crud.delete_by_id(id), Duration.Inf)
      }
    }

    // adds an ontology
    def add(filePath: String, fileName: String, fileMime: String,
            description: String,
            prefix: String, uri: String): Future[String] = {

      val rdf_file = Paths.get(filePath).toAbsolutePath()
      val add_src = Source(List(
        DataPart("name", prefix),
        DataPart("description", description),
        DataPart("baseUri", uri),
        FilePart("data", fileName, Option(fileMime), FileIO.fromPath(rdf_file))))

      val res = ws.url(urls.add_ontology)
        .withHeaders(("Accept", "application/json"))
        .withFollowRedirects(FOLLOW_REDIRECTS)
        .post(add_src)

      res.flatMap { response =>
        response.status match {
          case 200 =>
            val json = JSONHelper.read(response.body)
            val _id: String = json.get("ontologyId").textValue()
            Future.successful(_id)
          case 409 => Future.failed(new ResourceAlreadyExistsException(s"the ${prefix} resource already exists!"))
          case _   => Future.failed(new Exception(response.body))
        }
      }

    }

    // deletes an ontology, by id
    def delete_by_id(ontology_id: String): Future[String] = {

      ws.url(urls.delete_ontology(ontology_id))
        .withFollowRedirects(FOLLOW_REDIRECTS)
        .delete()
        .flatMap { res =>
          res.status match {
            case 200 => Future.successful(s"ontology with id ${ontology_id} deleted")
            case 404 => Future.failed(new ResourceNotExistsException)
            case _   => Future.failed(new Exception(res.body))
          }
        }

    }

  }

  object urls {

    // list of all the ontologies
    def ontologies_list = s"http://${host}:${port}/stanbol/ontonethub/ontologies"

    // find lookup url
    def find_ontologies() = s"http://${host}:${port}/stanbol/ontonethub/ontologies/find"

    // search on ontology, by ontology id
    def find_by_ontology_id(ontologyID: String) = s"http://${host}:${port}/stanbol/ontonethub/ontology/${ontologyID}/find"

    // ontology metadata
    def ontology_metadata(ontologyID: String) = s"http://${host}:${port}/stanbol/ontonethub/ontology/${ontologyID}"

    // ontology source (in JSON)
    def ontology_source(ontologyID: String) = s"http://${host}:${port}/stanbol/ontonethub/ontology/${ontologyID}/source"

    def add_ontology() = s"http://${host}:${port}/stanbol/ontonethub/ontology"

    def delete_ontology(onto_id: String) = s"http://${host}:${port}/stanbol/ontonethub/ontology/${onto_id}"

  }

}

class ResourceAlreadyExistsException(msg: String = "the resource already exists!") extends RuntimeException(msg)

class ResourceNotExistsException(msg: String = "the resource does not exists!") extends RuntimeException(msg)
