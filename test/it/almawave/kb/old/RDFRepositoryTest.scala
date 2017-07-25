package it.almawave.kb.old

import java.net.URI
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.nio.file.Paths
import java.io.ByteArrayInputStream
import org.junit.rules._
import java.io.InputStream

class RDFRepositoryTest {

  // TODO: create a test for each type
  //  val repo = RDFRepository.remote("http://localhost:9999/sparql")

  val repo = RDFRepository.memory()

  @Before
  def before() {
    println(s"\n${this} START")
    repo.start()
  }

  @After
  def after() {
    println(s"\n${this} STOP")
    repo.stop()
  }

  @Test
  def test_active() {
    Assert.assertNotNull(repo)
  }

  @Test
  def test_count() {
    Assert.assertEquals(0, repo.count("http://no_one"))
  }

  @Test
  def test_drop_graph() {
    val context_uri = "http://testing/dcat"
    repo.dropGraph(context_uri)
    //    Assert.assertTrue(!repo.existsGraph(context_uri))
    Assert.assertEquals(0, repo.count(context_uri))
  }

  @Test
  def test_load() {
    val dcat_local = Paths.get("ontologies/agid/DCAT-AP_IT/DCAT-AP_IT.owl").normalize().toUri()

    println("\n\n\n\n\nDCAT_LOCAL")
    println(dcat_local)

    val context_uri = "http://testing/dcat"
    Assert.assertEquals(0, repo.count(context_uri))
    repo.loadRDF(dcat_local, context_uri)
    Assert.assertTrue(repo.count(context_uri) > 0)
    val dcat_triples = repo.count(context_uri)
    Assert.assertEquals(909, dcat_triples)
    Assert.assertTrue(repo.existsGraph(context_uri))
  }

  @Test
  def test_load_remote() {
    val foaf_remote = new URI("http://xmlns.com/foaf/spec/index.rdf")
    val foaf_context = "http://xmlns.com/foaf/"
    repo.dropGraph(foaf_context)
    Assert.assertEquals(0, repo.count(foaf_context))
    repo.loadRDF(foaf_remote, foaf_context)
    Assert.assertTrue(repo.count(foaf_context) > 0)
    val foaf_triples = repo.count(foaf_context)
    Assert.assertEquals(620, foaf_triples)
  }

  // NOTE: at the moment we don't handle exceptions!
  //  @Test(expected = classOf[RuntimeException], timeout = 4000)
  //  def test_load_failed() {
  //    val rdfDocument = Paths.get("ontologies/agid/DCAT-AP_IT/DCAT-AP_IT.owl").normalize().toUri()
  //    //    val rdfDocument = new File("there/is/no/file.owl").toURI()
  //    val context_uri = "http://testing/dcat"
  //    repo.loadRDF(rdfDocument, context_uri)
  //        Assert.assertEquals(0, repo.count(context_uri))
  //  }

  @Test
  def test_load_remove() {

    val foaf_remote = new URI("http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    val foaf_context = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    Assert.assertEquals(0, repo.count(foaf_context))

    repo.loadRDF(foaf_remote, foaf_context)
    val triples = repo.count(foaf_context)
    println("how many triples? " + triples)
    Assert.assertTrue(repo.count(foaf_context) > 0)
    Assert.assertEquals(102, repo.count(foaf_context))

    //    disabled
    //    repo.removeRDF(foaf_remote, foaf_context)
    //    Assert.assertEquals(0, repo.count(foaf_context)) // why there are few more?
  }

  @Test
  def test_prefixes() {

    // check clear
    repo.clearPrefixes()
    Assert.assertEquals(0, repo.prefixes().size)

    // check defaults / add
    repo.prefixes(PREFIXES.default)
    Assert.assertTrue(repo.prefixes().size > 0)
    Assert.assertTrue(repo.prefixes().contains("rdf"))
    Assert.assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#", repo.prefixes().get("rdf").get)

    repo.clearPrefixes()
    repo.addPrefix("prefix", "http://testing/namespace")
    Assert.assertEquals(1, repo.prefixes().size)
    Assert.assertEquals("http://testing/namespace", repo.prefixes().get("prefix").get)
    repo.removePrefix("prefix")
    Assert.assertEquals(0, repo.prefixes().size)

  }

  @Test
  def basic_operations() {

    val size_before = repo.count()

    val dir_ontologies = "ontologies/"
    repo.importFrom(dir_ontologies)

    val size_after = repo.count()

    println(s"SIZE? before: ${size_before} after: ${size_after}")
    Assert.assertTrue(size_after > size_before)

    //    repo.clear()
    //    val size_after_clean = repo.count()
    //    Assert.assertEquals(size_before, size_after_clean)

  }

  @Test
  def test_graphs() {

    val baseURI = new URI("http://testing/graph-01")
    val bais: InputStream = new ByteArrayInputStream("""
      <http://testing/subject-01> <http://testing/predicate-01> <http://testing/object-01>
    """.getBytes)

    repo.addRDF(bais, baseURI, "N-TRIPLES", "http://testing/graph-01")

    // TODO: verify how to clean all

    repo.graphs()
  }

}