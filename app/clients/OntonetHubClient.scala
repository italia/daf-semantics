package clients

import play.api.libs.json.JsValue
import play.api.libs.json.JsLookupResult
import play.api.libs.ws.WSClient
import play.api.libs.json.JsString
import utilities.JSONHelper
import scala.concurrent.Future
import semantic_manager.yaml.OntonetHubProperty
import akka.stream.scaladsl.Source
import java.io.File
import play.api.mvc.MultipartFormData.DataPart
import play.api.mvc.MultipartFormData.FilePart
import akka.stream.scaladsl.FileIO

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import semantic_manager.yaml.OntologyMeta
import com.fasterxml.jackson.databind.JsonNode
import play.api.libs.json.Json

/**
 * TODO: add request logger
 *
 * CHECK: see how to de-couple the OntonetHubProperty case class produced automatically,
 * from the the case class needed for returning the results
 */
class OntonetHubClient(ws: WSClient) {

  import scala.concurrent.ExecutionContext.Implicits._

  implicit def toList(root: JsLookupResult): List[JsValue] = root.as[List[JsValue]]

  val (host, port) = ("localhost", 8000)
  val FOLLOW_REDIRECTS = true
  val PAUSE = 1000

  /**
   * this method can be used to check if the ontonethub service is running
   */
  def status(): Future[Boolean] = {
    ws.url(s"http://${host}:${port}/stanbol/ontonethub/")
      .withFollowRedirects(FOLLOW_REDIRECTS)
      .get()
      .map { response =>
        (response.status == 200) && response.body.contains("OntoNetHub")
      }
  }

  // REVIEW
  // returns a list of ontology uris from ontonethub
  //    def list_ids: Future[List[String]] = {
  //  def list_ids = {
  //    list_ontologies.map {
  //      list => list.map { _.get("ontologyID").asText() }
  //    }
  //  }

  def find_property(query: String, lang: String = "", limit: Int = 4) = {

    ws.url(s"http://${host}:${port}/stanbol/ontonethub/ontologies/find")
      .withHeaders(("accept", "application/json"))
      .withHeaders(("content-type", "application/x-www-form-urlencoded"))
      .withFollowRedirects(FOLLOW_REDIRECTS)
      .post(s"name=${query}&lang=${lang}&limit=${limit}")
      .map { res => res.json.\("results").toList }
      .map { list =>
        list.map { item =>
          val uri = item.\("id").get.as[JsString].value
          val label = item.\("rdfLabel").toList.head.\("value").get.as[JsString].value
          val refersTo = item.\("dafLabel").toList.head.\("value").get.as[JsString].value
          OntonetHubProperty(refersTo, uri, label, None, None, None)
        }
      }
  }

  /**
   *  adds an ontology
   */
  def add(rdf_file: File, fileName: String, fileMime: String,
          description: String,
          prefix: String, uri: String): Future[String] = {

    //      val rdf_file = Paths.get(filePath).toAbsolutePath()
    val add_src = Source(List(
      DataPart("name", prefix),
      DataPart("description", description),
      DataPart("baseUri", uri),
      FilePart("data", fileName, Option(fileMime), FileIO.fromPath(rdf_file.toPath()))))

    val res = ws.url(s"http://${host}:${port}/stanbol/ontonethub/ontology")
      .withHeaders(("Accept", "application/json"))
      .withFollowRedirects(FOLLOW_REDIRECTS)
      .post(add_src)

    // preparing results, with a minimal error handling
    // NOTE: we have to deal with two level of status: for resource and job
    res.flatMap { response =>
      response.status match {
        case 200 =>
          val json = JSONHelper.read(response.body)
          val _id: String = json.get("ontologyId").textValue()
          for {
            ok <- Future.successful(_id)
            status <- job_status(_id) // checking if the index has been updated
          } yield ok
        case 409 => Future.failed(new ResourceAlreadyExistsException(s"the ${prefix} resource already exists!"))
        case _   => Future.failed(new Exception(response.body))
      }
    }

  }

  /**
   * deletes an ontology, by id
   */
  def delete_by_id(ontologyID: String): Future[String] = {

    ws.url(s"http://${host}:${port}/stanbol/ontonethub/ontology/${ontologyID}")
      .withFollowRedirects(FOLLOW_REDIRECTS)
      .delete()
      .flatMap { res =>
        res.status match {
          case 200 =>
            for {
              ok <- Future.successful(s"ontology with id ${ontologyID} deleted")
              status <- job_status(ontologyID)
            } yield ontologyID

          case 404 => Future.failed(new ResourceNotExistsException)
          case _   => Future.failed(new Exception(res.body))
        }
      }

  }

  /**
   * checking the current status of the job after crud operations
   */
  def job_status(ontologyID: String): Future[String] = {
    ws.url(s"http://${host}:${port}/stanbol//jobs/${ontologyID}")
      .withFollowRedirects(FOLLOW_REDIRECTS)
      .get()
      .flatMap { response =>
        response.status match {
          case 200 =>
            val json = JSONHelper.read(response.body)
            json.get("status").textValue() match {
              // once an action has beeen requested, we have to check its status inside the JSON object
              case "finished" => Future.successful(s"ontology ${ontologyID} has been correctly uploaded")
              case "aborted"  => Future.failed(new Exception(response.body))
              case "running" =>
                // we introduce a pause to avoid too much requests
                Thread.sleep(PAUSE)
                job_status(ontologyID)
            }
          // if the resource id does not exists yet, we have to handle anHTTP error 
          case 404 => Future.failed(new ResourceNotExistsException(s"the ${ontologyID} resource does not exists!"))
          case _   => Future.failed(new Exception(response.body))
        }
      }
  }

  /**
   * returns a list of ontology ids
   */
  def list_ontologies = {
    ws.url(s"http://${host}:${port}/stanbol/ontonethub/ontologies")
      .withFollowRedirects(FOLLOW_REDIRECTS)
      .get()
      .map { res => res.json.as[List[JsValue]] }
    //      .map { response =>
    //        JSONHelper.read(response.body).elements().toList
    //      }
  }

}