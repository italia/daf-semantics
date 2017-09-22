package clients

import play.api.libs.json.JsValue
import play.api.libs.json.JsLookupResult
import play.api.libs.ws.WSClient
import play.api.libs.json.JsString
import utilities.JSONHelper
import scala.concurrent.Future
import akka.stream.scaladsl.Source
import java.io.File
import play.api.mvc.MultipartFormData.DataPart
import play.api.mvc.MultipartFormData.FilePart
import akka.stream.scaladsl.FileIO

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.fasterxml.jackson.databind.JsonNode
import play.api.libs.json.Json
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import play.api.libs.json.JsNumber
import scala.util.Try
import com.typesafe.config.ConfigResolveOptions
import com.typesafe.config.ConfigRenderOptions
import com.typesafe.config.ConfigParseOptions
import com.fasterxml.jackson.databind.ObjectMapper
import semantic_manager.yaml.OntologyMeta
import semantic_manager.yaml.OntonetHubProperty

/**
 * TODO: add request logger
 *
 * CHECK: see how to de-couple the OntonetHubProperty case class produced automatically,
 * from the the case class needed for returning the results
 *
 * TODO:
 * 	- check parameters order in case class created from swagger
 * 	- de-coupling models from swagger models (SEE: OntonetHubClient.models)
 */
class OntonetHubClient(ws: WSClient, conf: Config = OntonetHubClient.DEFAULT_CONFIG) {

  import scala.concurrent.ExecutionContext.Implicits._
  import OntonetHubClient.models._

  implicit def toList(root: JsLookupResult): List[JsValue] = root.as[List[JsValue]]

  val (host, port) = (conf.getString("host"), conf.getLong("port"))
  val FOLLOW_REDIRECTS = conf.getBoolean("follow_redirects")
  val PAUSE = conf.getLong("pause")

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

  val json_mapper = new ObjectMapper
  val json_reader = json_mapper.reader()

  def find_property(query: String, lang: String = "", limit: Int = 10) = {

    ws.url(s"http://${host}:${port}/stanbol/ontonethub/ontologies/find")
      .withHeaders(("accept", "application/json"))
      .withHeaders(("content-type", "application/x-www-form-urlencoded"))
      .withFollowRedirects(FOLLOW_REDIRECTS)
      .post(s"name=${query}&lang=${lang}&limit=${limit}")
      .map { res => res.json.\("results").toList }
      .map { list =>
        list.map { item =>

          val id = Try { item.\("id").get.as[JsString].value }.getOrElse("")
          val dafLabel = Try { item.\("dafLabel").toList.head.\("value").get.as[JsString].value }.getOrElse("")
          val label = Try { item.\("label").toList.head.\("value").get.as[JsString].value }.getOrElse("")
          val comment = Try { item.\("comment").toList.head.\("value").get.as[JsString].value }.getOrElse("")
          //  DISABLED val dafId = Try { item.\("dafId").toList.head.\("value").get.as[JsString].value }.getOrElse("")
          val score = Try { item.\("score").toList.head.\("value").get.as[JsNumber].value.toDouble }.getOrElse(0.0)
          val class_label = Try { item.\("label.class").toList.head.\("value").get.as[JsString].value }.getOrElse("")
          val class_comment = Try { item.\("comment.class").toList.head.\("value").get.as[JsString].value }.getOrElse("")
          val ontology_label = Try { item.\("label.ontology").toList.head.\("value").get.as[JsString].value }.getOrElse("")
          val ontology_comment = Try { item.\("comment.ontology").toList.head.\("value").get.as[JsString].value }.getOrElse("")

          OntonetHubProperty(
            dafLabel,
            ontology_comment,
            comment,
            ontology_label,
            id,
            score.toString(),
            label: String,
            class_label,
            class_comment)

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
   * TODO:	avoid using directly the case class models generated from swagger here.
   * 				an idea could be using something like CaseClass.tupled()
   */
  def list_ontologies = {

    import play.api.libs.json.Reads._

    implicit val ontoJsonFormat = Json.format[OntologyMeta]
    ws.url(s"http://${host}:${port}/stanbol/ontonethub/ontologies")
      .get()
      // CHECK: de-coupling models from swagger case classes
      //      .map(_.json.as[Array[Ontology]])
      .map { res => res.json.as[Array[OntologyMeta]] }
  }

}

object OntonetHubClient {

  val DEFAULT_CONFIG = ConfigFactory.parseString("""
    host: localhost
  	port: 8000
  	follow_redirects: true
  	pause: 1000
	""")

  object models {

    case class FindResult(id: String, dafLabel: String, label: String, comment: String, score: String, class_label: String, class_comment: String, ontology_label: String, ontology_comment: String)

    case class Ontology(objectProperties: Int, ontologyDescription: String, ontologyIRI: String, ontologySource: String, importedOntologies: Int, owlClasses: Int, datatypeProperties: Int, ontologyName: String, individuals: Int, ontologyID: String, annotationProperties: Int)

  }

}


