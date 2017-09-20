package review.ontonethub

import play.api.libs.ws.WSClient
import scala.concurrent.Future
import akka.stream.scaladsl.Source
import play.api.mvc.MultipartFormData.DataPart
import play.api.mvc.MultipartFormData.FilePart
import akka.stream.scaladsl.FileIO
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import utilities.Adapters._
import utilities.JSONHelper
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import clients.ResourceAlreadyExistsException
import java.io.File
import clients.ResourceNotExistsException

object OntonethubClient2 {

  def create(implicit ws: WSClient) = new OntonethubClient2(ws)

}

/**
 * This is a Client for wrapping OntonetHub component (based on Stanbol).
 * The idea behind it is to expose simple API for simpler interaction and integration
 * of OntonetHub/Stanbol with the other components handling semantics (triplestores...)
 *
 * TODO: refactorization, after local testing
 */
class OntonethubClient2(ws: WSClient) {

  import scala.concurrent.ExecutionContext.Implicits._

  // TODO: export the configurations
  val host = "localhost"
  val port = 8000
  val FOLLOW_REDIRECTS = true
  val PAUSE = 1000

  /**
   * this object encapsulates various types of lookup
   */
  object lookup {

    /**
     * returns the id for the given prefix
     */
    //    def find_id_by_prefix(prefix: String): Future[String] = {
    //      ids_for_prefixes
    //        //        .map { map => map.toString() }
    //        .map { map => map.get(prefix).get }
    //    }

    /**
     * returns a list of (prefix, id) pair
     */
    //    private def ids_for_prefixes() = {

    //      list_ids.flatMap { ids_list =>
    //
    //        // for each ontology: retrieves the name associated with id
    //        val temp = ids_list.map { onto_id =>
    //          ws.url(urls.ontology_metadata(onto_id.toString()))
    //            .withFollowRedirects(FOLLOW_REDIRECTS)
    //            .get()
    //            .map { res => JSONHelper.read(res.body) }
    //            .map { json => (json.get("name").asText(), json.get("id").asText()) }
    //        }
    //
    //        Future.sequence(temp).map(_.toMap)
    //
    //      }

    //    }

  }

  /**
   *  this object encapsulates the methods for minimal CRUD operations
   */
  object crud {

    /**
     * retrieves the content of an ontology
     * NOTE: this is not the original source file of the ontology itself
     */
    def get(ontologyID: String, mime: String = "text/turtle"): Future[String] = {

      ws.url(urls.ontology_source(ontologyID))
        .withFollowRedirects(FOLLOW_REDIRECTS)
        .withHeaders(("Accept", mime))
        .get()
        .map { res => res.body }

    }

    /**
     *  deletes all the loaded ontologies
     */
    def clear() = {
      //      val results = lookup.list_ids.map { ids => ids.map { id => crud.delete_by_id(id.toString()) } }
      //      Await.result(results, Duration.Inf)
    }

  }

  /**
   * this is an helper object to simplifying specific endpoint uris handling.
   * TODO: externalize these configurations!
   */
  object urls {

    def job_status(ontologyID: String) = s"http://${host}:${port}/stanbol//jobs/${ontologyID}"

    // list of all the ontologies
    def ontologies_list = s"http://${host}:${port}/stanbol/ontonethub/ontologies"

    // find lookup url
    def find() = s"http://${host}:${port}/stanbol/ontonethub/ontologies/find"

    // search on ontology, by ontology id
    def find_by_id(ontologyID: String) = s"http://${host}:${port}/stanbol/ontonethub/ontology/${ontologyID}/find"

    // ontology metadata
    def ontology_metadata(ontologyID: String) = s"http://${host}:${port}/stanbol/ontonethub/ontology/${ontologyID}"

    // ontology source (in JSON)
    def ontology_source(ontologyID: String) = s"http://${host}:${port}/stanbol/ontonethub/ontology/${ontologyID}/source"

    def add_ontology() = s"http://${host}:${port}/stanbol/ontonethub/ontology"

    def delete_ontology(onto_id: String) = s"http://${host}:${port}/stanbol/ontonethub/ontology/${onto_id}"

    def status = s"http://${host}:${port}/stanbol/ontonethub/"

  }

}

