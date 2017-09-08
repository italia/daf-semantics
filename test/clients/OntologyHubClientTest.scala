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

/**
 * TODO: FIX and expand tests
 */
class OntologyHubClientTest {

  val logger = LoggerFactory.getLogger(this.getClass)

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

    logger.debug("\n\nontonethub - add / status / list")

    // adds an ontology
    val add_fut = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri)
    val id = Await.result(add_fut, Duration.Inf)
    logger.debug("ADDED: " + id)

    // verify the ontology has been added
    val ids = Await.result(ontonethub.lookup.list_ids, Duration.Inf)
    logger.debug(s"IDS list: [${ids.mkString(" | ")}]")

    Assert.assertTrue(ids.contains(id))
    Assert.assertEquals(1, ids.size)

  }

  @Test(expected = classOf[ResourceAlreadyExistsException])
  def add_duplicated() {

    logger.debug("\n\nontonethub - add duplicated")

    // adds
    var add_fut = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri)
    var id = Await.result(add_fut, Duration.Inf)
    // adds again
    add_fut = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri)
    id = Await.result(add_fut, Duration.Inf)
  }

  @Test
  def add_remove_by_id() = {

    logger.debug("\n\nontonethub - add / remove ontology")

    var id: String = null

    // add
    try {
      val add_fut = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri)
      id = Await.result(add_fut, Duration.Inf)
      logger.debug(s"ADD ontology: ${id}")
    } catch {
      case ex: Throwable => Assert.fail("error in adding ontology" + ex)
    }

    // status
    try {
      val status = Await.result(ontonethub.lookup.status(id), Duration.Inf)
      logger.debug("STATUS: " + status)
    } catch {
      case ex: Throwable => Assert.fail("error getting status for ${id}" + ex)
    }

    // remove
    try {
      val del_fut = ontonethub.crud.delete_by_id(id)
      val del_res = Await.result(del_fut, Duration.Inf)
      logger.debug(s"REMOVE ontology: ${id}")
    } catch {
      case ex: Throwable => Assert.fail("error in adding ontology" + ex)
    }

  }

  @Test
  def find_id_by_prefix() {

    logger.debug(s"\n\nontonethub - find ontology id by prefix ${prefix}")

    val add_fut = ontonethub.crud.add(filePath, fileName, fileMime, description, prefix, uri)
    val id = Await.result(add_fut, Duration.Inf)
    logger.debug(s"ADDED: ${id}")

    val find_fut = ontonethub.lookup.find_id_by_prefix(prefix)
    val found_id = Await.result(find_fut, Duration.Inf)
    logger.debug(found_id)

    Assert.assertEquals(id, found_id)
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
    Assume.assumeTrue(ontonethub.status())
    client.stop()
    logger.info("Ontonethub is UP! [TESTING...]")
  }

}