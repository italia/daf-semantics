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

class OntologyHubClientTest {

  import scala.concurrent.ExecutionContext.Implicits._

  val client = HTTPClient

  var ontonethub: OntonethubClient = null

  // example data
  //  val filePath = "C:/Users/Al.Serafini/awavedev/workspace_playground/kb-core-experiments/ontologies/foaf/foaf.rdf"
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
    //    ontonethub.crud.clear()
    client.stop()
  }

  //  @Test
  def clear() {
    ontonethub.crud.clear()
    val list = Await.result(ontonethub.lookup.list_ids, Duration.Inf)
    Assert.assertEquals(0, list.size)
  }

  @Test
  def list_ontologies_ids() {

    ontonethub.crud.clear()

    // VERIFY how to rewrite here...
    //    val id = for {
    //      _id <- ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri)
    //      status <- ontonethub.lookup.status(_id)
    //    } yield _id

    val add_fut = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri)

    val id = Await.result(add_fut, Duration.Inf)
    println("ADDED: " + id)

    val status = Await.result(ontonethub.lookup.status(id), Duration.Inf)
    println("STATUS: " + status)

    val ids = Await.result(ontonethub.lookup.list_ids, Duration.Inf)
    println(s"IDS list: [${ids.mkString(" | ")}]")

    Assert.assertTrue(ids.contains(id))
    Assert.assertEquals(1, ids.size)

  }

  @Test
  def list_ontologies_uris() {
    val list_fut = ontonethub.lookup.list_uris
    val list = Await.result(list_fut, Duration.Inf)
    println("\n\nontonethub - list of ontologies")
    println(list.mkString(" | "))
  }

  //  @Test
  def add_new_ontology() {

    val add_fut = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri)
    println("\n\nontonethub - add new ontology")
    val add_res = Await.result(add_fut, Duration.Inf)
    println("ADD ONTOLOGY: " + add_res)

  }

  //  @Test
  def delete_ontology_by_id() = {
    val ids: List[String] = Await.result(ontonethub.lookup.list_ids, Duration.Inf)
    val _id = ids.head
    println(s"\n\nontonethub - delete ontology by id: ${_id}")
    val del_fut = ontonethub.crud.delete_by_id(_id)
    del_fut.onFailure { case ex: Throwable => println(ex) }
    val del_res = Await.result(del_fut, Duration.Inf)
    println(del_res)
  }

  //  @Test
  def find_id_by_prefix() {
    val _prefix = "foaf"
    println(s"\n\nontonethub - find ontology id by prefix ${_prefix}")
    val _id = ontonethub.lookup.find_id_by_prefix(_prefix).get
    println(_id)
  }

  //  @Test
  def add_remove_ontology() {

    val filePath = "C:/Users/Al.Serafini/awavedev/workspace_playground/kb-core-experiments/ontologies/foaf/foaf.rdf"
    val fileName = "example.rdf"
    val fileMime = "application/rdf+xml"
    val description = "foaf ontology"

    val prefix = "example_024"
    val uri = "http://example.org/"

    println("\n\nontonethub - add/remove ontology")
    val add_fut = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri)
    var ID = Await.result(add_fut, Duration.Inf) // VERIFY...

    println("NEW ID: " + ID)

    val del_fut = ontonethub.crud.delete_by_id(ID)
    val del_res = Await.result(del_fut, Duration.Inf)
    println(s"the resource with id ${del_res} was correctly added and remove!")
  }

}
