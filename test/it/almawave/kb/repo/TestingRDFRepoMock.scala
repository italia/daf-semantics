package it.almawave.kb.repo

import java.io.FileInputStream
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.rio.Rio
import org.junit.Test
import org.junit.Before
import org.junit.After
import org.junit.Assert
import java.io.StringReader

class TestingRDFRepoMock {

  // TODO: move in config
  System.setProperty("org.eclipse.rdf4j.repository.debug", "true")

  val base_uri = "http://local/graph/"

  val vfm = SimpleValueFactory.getInstance
  val contexts = List(
    vfm.createIRI("http://localhost"),
    vfm.createIRI("http://graph"))

  val doc_example = """
      @prefix foaf: <http://xmlns.com/foaf/0.1/> .
      @prefix ex: <http://example.org/> .
      ex:john a foaf:Person ;
        foaf:name "John" ;
        foaf:age 42 ;
        foaf:mbox "john@example.org" 
      .
    """

  val rdf_doc_artists = Rio.parse(new FileInputStream("ontologies/examples/example-data-artists.ttl"), base_uri, RDFFormat.TURTLE)

  val rdf_doc_simple = Rio.parse(new StringReader(doc_example), "http://example.org/", RDFFormat.TURTLE)

  //  var db: Repository = null
  var mock = RDFRepository.memory()

  @Before()
  def before() {

    mock.start()

  }

  @After()
  def after() {

    mock.stop()

  }

  @Test
  def size_when_empty() {
    mock.store.clear()
    Assert.assertEquals(0, mock.store.size())
  }

  @Test
  def contexts_list() {

    val vf = SimpleValueFactory.getInstance
    val ctxs = Seq("http://localhost", "http://graph").map { cx => vf.createIRI(cx) }
    mock.store.add(rdf_doc_simple, ctxs: _*)

    val expected_list = Seq("http://graph", "http://localhost")
    val contexts_list = mock.store.contexts()

    Assert.assertEquals(expected_list.size, contexts_list.size)

    contexts_list.foreach { ctx =>
      Assert.assertTrue(expected_list.contains(ctx))
    }

    mock.store.clear()

  }

  @Test
  def add_file() {

    // adds on multiple contexts
    Assert.assertTrue(rdf_doc_artists.size() > 0)

    mock.store.clear(contexts: _*)

    val size_before = mock.store.size(contexts: _*)
    Assert.assertEquals(0, size_before)

    mock.store.add(rdf_doc_artists, contexts: _*)

    val size_after = mock.store.size(contexts: _*)
    Assert.assertTrue(size_before < size_after)

    val estimated_size = rdf_doc_artists.size() * contexts.size
    Assert.assertEquals(size_after - size_before, estimated_size)

    // adds with no specified context
    mock.store.clear()
    Assert.assertEquals(0, mock.store.size())
    mock.store.add(rdf_doc_artists)
    Assert.assertEquals(rdf_doc_artists.size(), mock.store.size())

    // adds to a single context
    mock.store.clear(contexts(0))
    Assert.assertEquals(0, mock.store.size(contexts(0)))
    mock.store.add(rdf_doc_artists, contexts(0))
    Assert.assertEquals(rdf_doc_artists.size(), mock.store.size(contexts(0)))

  }

  @Test
  def remove_file() {

    Assert.assertTrue(rdf_doc_artists.size() > 0)

    val size_before = mock.store.size()
    Assert.assertEquals(size_before, 0)

    mock.store.remove(rdf_doc_artists, contexts: _*)
    val size_after = mock.store.size()

    Assert.assertEquals(size_before, size_after)

  }

  //  @Test
  def add_remove_from_context() {

    mock.store.clear(contexts: _*)
    Assert.assertEquals(0, mock.store.size(contexts: _*))

    val rdf_doc_01 = Rio.parse(new FileInputStream("ontologies/examples/example-data-artists.ttl"), base_uri, RDFFormat.TURTLE) // ALL CONTEXTS!
    Assert.assertTrue(rdf_doc_01.size() > 0)

    val rdf_doc_02 = Rio.parse(new FileInputStream("ontologies/examples/example-data-artists.ttl"), base_uri, RDFFormat.TURTLE) // A SINGLE CONTEXTS!
    Assert.assertTrue(rdf_doc_02.size() > 0)

    val rdf_doc_03 = rdf_doc_artists // NO CONTEXT!
    Assert.assertTrue(rdf_doc_03.size() > 0)

    // the second version of the parsed document will have less materialized triples!
    Assert.assertTrue(rdf_doc_01.size() > rdf_doc_02.size())

    val size_before = mock.store.size()
    Assert.assertEquals(size_before, 0)

    // adds the document to each context
    mock.store.add(rdf_doc_01, contexts: _*)
    mock.store.remove(rdf_doc_02, contexts(0))
    Assert.assertEquals(0, mock.store.size(contexts(0)))
    Assert.assertEquals(rdf_doc_01.size(), mock.store.size(contexts(1)))

    mock.store.clear()
    mock.store.add(rdf_doc_03, contexts: _*)
    Assert.assertEquals(mock.store.size(), rdf_doc_03.size())

    mock.store.clear()
    mock.store.add(rdf_doc_02)
    Assert.assertEquals(mock.store.size(), rdf_doc_02.size())

    mock.store.clear()
    mock.store.add(rdf_doc_01)
    Assert.assertEquals(mock.store.size(), rdf_doc_01.size())

  }

  @Test
  def sparql_query() {

    mock.store.add(rdf_doc_artists, contexts: _*)
    println("added " + mock.store.size(contexts: _*))

    // TODO: externalize
    val query = s"""
      SELECT * 
      # FROM DEFAULT   
      FROM NAMED <${contexts(0)}>
      FROM NAMED <${contexts(1)}>
      WHERE {
        GRAPH ?g {
          ?s ?p ?o
        }
      }
    """

    val size = mock.sparql.query(query).size
    println(s"---- ${size} TOTAL SPARQL results")

    Assert.assertEquals(mock.store.size(contexts: _*), mock.sparql.query(query).size)

  }

  @Test
  def test_import() {
    mock.helper.importFrom("ontologies")
    val vf = SimpleValueFactory.getInstance
    val size = mock.store.size(vf.createIRI("http://xmlns.com/foaf/0.1/"))
    println("SIZE: " + size)
  }

}

object ExamplesContextWithSPARQL extends App {

  //  // ------------- HELPER -------------
  //
  //  // TODO: move in helper class
  //  implicit class TupleResultIterator(result: TupleQueryResult) extends Iterator[BindingSet] {
  //    def hasNext: Boolean = result.hasNext()
  //    def next(): BindingSet = result.next()
  //  }
  //
  //  // TODO: move in helper class
  //  implicit class RepositoryResultIterator[T](result: RepositoryResult[T]) extends Iterator[T] {
  //    def hasNext: Boolean = result.hasNext()
  //    def next(): T = result.next()
  //  }
  //
  //  // ------------- HELPER -------------
  //
  //  // SEE: http://docs.rdf4j.org/rdf-tutorial/
  //

  //

  //
  //  // ----------------------------------------------------------------------------------------------
  //
  //  val db: Repository = new SailRepository(new MemoryStore())
  //  db.initialize();
  //
  //  var conn = db.getConnection()
  //  val vf = conn.getValueFactory // CHECK: providing custom implementation for BN
  //
  //  conn.add(model, contexts: _*)
  //
  //  conn.close()
  //  conn = db.getConnection
  //
  //  val statements = conn.getStatements(null, null, null, false, contexts: _*)
  //
  //  println("\n\n\n\n#### STATEMENTS")
  //  var count = 0
  //  while (statements.hasNext()) {
  //
  //    val st = statements.next()
  //    count += 1
  //    println(count + ": " + st)
  //
  //  }
  //
  //  println(s"#### ${count} statements")
  //
  //  conn.close()
  //
  //  conn = db.getConnection
  //
  //  // ------------ SPARQL -------------
  //
  //  println("\n\n\n\nSPARQL TESTS")
  //
  //  // TESTING: DROP GRAPH
  //  //  val queryDropGraph = s"""
  //  //    DROP GRAPH <${contexts(0)}>
  //  //  """
  //  //  conn.prepareUpdate(QueryLanguage.SPARQL, queryDropGraph).execute()
  //
  //  // TESTING: delete
  //  //  val queryDeleteGraph = s"""
  //  //  WITH <${contexts(0)}>
  //  //  DELETE { ?s ?p ?o }
  //  //  WHERE { ?s ?p ?o } 
  //  //  """
  //  //  conn.prepareUpdate(QueryLanguage.SPARQL, queryDeleteGraph).execute()
  //
  //  // TESTING: update
  //  //  val queryDeleteGraph = s"""
  //  //  WITH <${contexts(0)}>
  //  //  DELETE { ?s ?p ?o }
  //  //  INSERT { <http://graph/sub_01> ?p ?o }
  //  //  WHERE { ?s ?p ?o } 
  //  //  """
  //  //  conn.prepareUpdate(QueryLanguage.SPARQL, queryDeleteGraph).execute()
  //
  //  val query = s"""
  //    SELECT * 
  //    FROM DEFAULT 
  //    FROM NAMED <${contexts(0)}>
  //    WHERE {
  //      GRAPH <${contexts(0)}> {
  //        ?s ?p ?o
  //      }
  //    }
  //  """
  //  var i = 0
  //  val results = conn.prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate()
  //  while (results.hasNext()) {
  //    val res = results.next()
  //    i += 1
  //    println(i + ": " + res)
  //  }
  //  println(s"---- ${i} TOTAL SPARQL results")
  //
  //  // ------------ SPARQL -------------  
  //
  //  conn.close()
  //  db.shutDown()

}