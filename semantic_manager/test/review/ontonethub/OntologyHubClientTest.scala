package review.ontonethub

import java.nio.file.Paths

import org.junit.After
import org.junit.Assert
import org.junit.Assume
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.slf4j.LoggerFactory

import clients.HTTPClient
import play.Logger
import utilities.Adapters.AwaitFuture
import clients.OntonetHubClient
import clients.ResourceAlreadyExistsException
import play.api.libs.json.Json
import semantic_manager.yaml.OntologyMeta
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * TODO: FIX and expand tests coverage
 */
class OntologyHubClientTest {

  //  val logger = LoggerFactory.getLogger(this.getClass)
  val logger = Logger.underlying()

  var client = HTTPClient

  var ontonethub: OntonetHubClient = null

  // example data
  val filePath = Paths.get("dist/data/ontologies/foaf/foaf.rdf").toAbsolutePath().toFile()
  val fileName = "foaf_test.rdf"
  val fileMime = "application/rdf+xml"
  val description = "foaf ontology"
  val prefix = "foaf_test_5"
  val uri = "http://foaf.org/"

  @Before
  def before() {

    client.start()

    ontonethub = new OntonetHubClient(client.ws)

    // TODO   ontonethub.crud.clear()
  }

  @After
  def after() {

    client.stop()

    // TODO   ontonethub.crud.clear()
  }

  @Test
  def clear() {
    //    ontonethub.crud.clear()
    //    val list = ontonethub.lookup.list_ids.await
    //    Assert.assertEquals(0, list.size)
  }

  @Test
  def list_preloaded_ontologies() {
    val ontologies = ontonethub.list_ontologies.await
    println("ONTOLOGIES LIST")
    println(ontologies.mkString("\n"))
    Assert.assertTrue(ontologies.size > 0)
  }

  //  @Test
  def find_id_by_prefix() {

    //    logger.debug(s"\n\nontonethub - find ontology id by prefix ${prefix}")
    //
    //    val id = ontonethub.crud
    //      .add(filePath, fileName, fileMime, description, prefix, uri)
    //      .await
    //
    //    logger.debug(s"added ontology with prefix: ${prefix} and id: ${id}")
    //
    //    val found_id = ontonethub.lookup
    //      .find_id_by_prefix(prefix)
    //      .await
    //
    //    logger.debug(s"found ontology with id: ${found_id}")
    //
    //    Assert.assertEquals(id, found_id)
  }

  @Test
  def add_status_list() {

    logger.debug("\n\nontonethub - add / status / list")

    // adds an ontology
    val id = ontonethub.add(filePath, fileName, fileMime, description, prefix, uri).await
    logger.debug(s"added ontology with prefix and ${prefix} with id: ${id}")

    //    // verify the ontology has been added
    //    val ids = ontonethub.list_ids.await
    //    logger.debug(s"IDS list: [${ids.mkString(" | ")}]")

    //    Assert.assertTrue(ids.contains(id))
    //    Assert.assertTrue(ids.size > 0)

  }

  //  @Test(expected = classOf[ResourceAlreadyExistsException])
  def add_duplicated() {

    logger.debug("\n\nontonethub - add duplicated")

    // adds
    var id = ontonethub.add(filePath, fileName, fileMime, description, prefix, uri).await
    println("added : " + id)

    // adds again!
    id = ontonethub.add(filePath, fileName, fileMime, description, prefix, uri).await
    println("added : " + id)

  }

  //  @Test
  def add_remove_by_id() = {

    logger.debug("\n\nontonethub - add / remove ontology")

    var id: String = null

    // add
    try {
      id = ontonethub.add(filePath, fileName, fileMime, description, prefix, uri).await
      logger.debug(s"ADD ontology: ${id}")
    } catch {
      case ex: Throwable =>
        Assert.fail("error in adding ontology\n" + ex)
    }

    // status
    try {
      val status = ontonethub.job_status(id).await
      logger.debug(s"checking status for ${id}: ${status}")
    } catch {
      case ex: Throwable => Assert.fail(s"error getting status for ${id}\n" + ex)
    }

    // remove
    try {
      val del_id = ontonethub.delete_by_id(id).await
      logger.debug(s"the ontology with id: ${id} was correctly removed")
    } catch {
      case ex: Throwable => Assert.fail(s"error in deleting ontology ${id}" + ex)
    }

  }

  @Test
  def find() {

    // find...
    val results = ontonethub.find_property("nome", "it", 10).await
    println("RESULTS: ")
    println(results.mkString("\n"))

    Assert.assertTrue(results.size > 0)

  }

}

/**
 *  checking if the service is up, for integration
 */
object OntologyHubClientTest {

  val logger = LoggerFactory.getLogger(this.getClass)

  @BeforeClass
  def check_before() {
    Assume.assumeTrue(ontonethub_is_running)
    logger.info("Ontonethub is UP! [TESTING...]")
  }

  private def ontonethub_is_running = {
    val client = HTTPClient
    client.start()
    val ontonethub = new OntonetHubClient(client.ws)
    val check = ontonethub.status().await
    client.stop()
    check
  }

}