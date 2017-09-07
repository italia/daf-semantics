package check.rdf4j.ws

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws._
import play.api.libs.ws.ahc.AhcWSClient
import scala.concurrent.Future
import modules.OntonethubClient

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

import it.almawave.kb.utils.JSONHelper
import akka.stream.scaladsl.Source
import play.api.mvc.MultipartFormData.DataPart
import scala.util.Success
import scala.util.Failure
import play.api.mvc.MultipartFormData.FilePart
import java.io.File
import akka.stream.scaladsl.FileIO
import java.nio.file.Paths
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Assert
import clients.HTTPClient
import scala.util.Try
import modules.ResourceAlreadyExistsException

/**
 * TODO: FIX and expand tests
 */
class OntologyHubClientTest {

  import scala.concurrent.ExecutionContext.Implicits._

  val client = HTTPClient

  var ontonethub: OntonethubClient = null

  // example data
  val filePath = "dist/data/ontologies/foaf/foaf.rdf"
  val fileName = "foaf.rdf"
  val fileMime = "application/rdf+xml"
  val description = "foaf ontology"
  val prefix = "foaf"
  val uri = "http://foaf.org/"

  @Before
  def before() {
    client.start()
    ontonethub = OntonethubClient.create(client.ws)
  }

  @After
  def after() {
    ontonethub.crud.clear()
    client.stop()
  }

  @Test
  def clear() {
    ontonethub.crud.clear()
    val list = Await.result(ontonethub.lookup.list_ids, Duration.Inf)
    Assert.assertEquals(0, list.size)
  }

  @Test
  def add_status_list() {

    println("\n\nontonethub - add / status / list")

    // adds an ontology
    val add_fut = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri)
    val id = Await.result(add_fut, Duration.Inf)
    println("ADDED: " + id)

    // verify the ontology has been added
    val ids = Await.result(ontonethub.lookup.list_ids, Duration.Inf)
    println(s"IDS list: [${ids.mkString(" | ")}]")

    Assert.assertTrue(ids.contains(id))
    Assert.assertEquals(1, ids.size)

  }

  @Test(expected = classOf[ResourceAlreadyExistsException])
  def add_duplicated() {

    println("\n\nontonethub - add duplicated")

    // adds
    var add_fut = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri)
    var id = Await.result(add_fut, Duration.Inf)
    // adds again
    add_fut = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri)
    id = Await.result(add_fut, Duration.Inf)
  }

  @Test
  def add_remove_by_id() = {

    println("\n\nontonethub - add / remove ontology")

    var id: String = null

    // add
    try {
      val add_fut = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri)
      id = Await.result(add_fut, Duration.Inf)
      println(s"ADD ontology: ${id}")
    } catch {
      case ex: Throwable => Assert.fail("error in adding ontology" + ex)
    }

    // status
    try {
      val status = Await.result(ontonethub.lookup.status(id), Duration.Inf)
      println("STATUS: " + status)
    } catch {
      case ex: Throwable => Assert.fail("error getting status for ${id}" + ex)
    }

    // remove
    try {
      val del_fut = ontonethub.crud.delete_by_id(id)
      val del_res = Await.result(del_fut, Duration.Inf)
      println(s"REMOVE ontology: ${id}")
    } catch {
      case ex: Throwable => Assert.fail("error in adding ontology" + ex)
    }

  }

  @Test
  def find_id_by_prefix() {

    println(s"\n\nontonethub - find ontology id by prefix ${prefix}")

    val add_fut = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri)
    val id = Await.result(add_fut, Duration.Inf)
    println(s"ADDED: ${id}")

    val find_fut = ontonethub.lookup.find_id_by_prefix(prefix)
    val found_id = Await.result(find_fut, Duration.Inf)
    println(found_id)

    Assert.assertEquals(id, found_id)
  }

}
