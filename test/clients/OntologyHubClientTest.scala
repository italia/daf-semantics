package clients

import play.api.libs.ws._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Assume
import org.slf4j.LoggerFactory
import modules.clients.ResourceAlreadyExistsException
import modules.clients.OntonethubClient

import play.Logger

import it.almawave.kb.utils.TryHandlers
import it.almawave.kb.utils.TryHandlers._

/**
 * TODO: FIX and expand tests coverage
 */
class OntologyHubClientTest {

  //  val logger = LoggerFactory.getLogger(this.getClass)
  val logger = Logger.underlying()

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
  def find_id_by_prefix() {

    logger.debug(s"\n\nontonethub - find ontology id by prefix ${prefix}")

    val id = ontonethub.crud
      .add(filePath, fileName, fileMime, description, prefix, uri)
      .await

    logger.debug(s"added ontology with prefix: ${prefix} and id: ${id}")

    val found_id = ontonethub.lookup
      .find_id_by_prefix(prefix)
      .await

    logger.debug(s"found ontology with id: ${found_id}")

    Assert.assertEquals(id, found_id)
  }

  @Test
  def add_status_list() {

    logger.debug("\n\nontonethub - add / status / list")

    // adds an ontology
    val id = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri).await
    logger.debug(s"added ontology with prefix and ${prefix} with id: ${id}")

    // verify the ontology has been added
    val ids = ontonethub.lookup.list_ids.await
    logger.debug(s"IDS list: [${ids.mkString(" | ")}]")

    Assert.assertTrue(ids.contains(id))
    Assert.assertEquals(1, ids.size)

  }

  @Test(expected = classOf[ResourceAlreadyExistsException])
  def add_duplicated() {

    logger.debug("\n\nontonethub - add duplicated")

    // adds
    var id = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri).await
    // adds again!
    id = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri).await

  }

  @Test
  def add_remove_by_id() = {

    logger.debug("\n\nontonethub - add / remove ontology")

    var id: String = null

    // add
    try {
      id = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri).await
      logger.debug(s"ADD ontology: ${id}")
    } catch {
      case ex: Throwable => Assert.fail("error in adding ontology\n" + ex)
    }

    // status
    try {
      val status = ontonethub.lookup.status(id).await
      logger.debug(s"checking status for ${id}: ${status}")
    } catch {
      case ex: Throwable => Assert.fail(s"error getting status for ${id}\n" + ex)
    }

    // remove
    try {
      val del_id = ontonethub.crud.delete_by_id(id).await
      logger.debug(s"the ontology with id: ${id} was correctly removed")
    } catch {
      case ex: Throwable => Assert.fail("error in adding ontology" + ex)
    }

  }

  @Test
  def find() {

    println("\n\n FIND RESULTS: ")

    //  IDEA  ontonethub.crud.find("citta")
    //  curl 
    //    -H  "accept: application/json" 
    //    -H  "content-type: application/x-www-form-urlencoded" 
    //    -X POST "http://localhost:8000/stanbol/ontonethub/ontologies/find" 
    //    -d "name=foaf"

    // adds an ontology
    val id = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri).await
    logger.debug(s"added ontology with prefix and ${prefix} with id: ${id}")

    // find...

    val results = ontonethub.crud.find("name", 10, "").await
    println("RESULTS: ")
    println(results)

  }

}

// check if the service is up, for integration
object OntologyHubClientTest {

  val logger = LoggerFactory.getLogger(this.getClass)

  @BeforeClass
  def check_before() {
    val client = HTTPClient
    client.start()
    val ontonethub = OntonethubClient.create(client.ws)
    Assume.assumeTrue(ontonethub.status().await)
    client.stop()
    logger.info("Ontonethub is UP! [TESTING...]")
  }

}