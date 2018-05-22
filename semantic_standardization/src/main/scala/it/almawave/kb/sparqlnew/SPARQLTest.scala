package it.almawave.kb.sparqlnew

import it.almawave.linkeddata.kb.file.RDFFileSail
import it.almawave.linkeddata.kb.file.RDFFileRepository
import org.eclipse.rdf4j.rio.Rio
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.eclipse.rdf4j.rio.RDFFormat
import java.nio.file.Paths
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Assert

// SOURCE: https://www.w3.org/TR/turtle/

class SPARQLTest {

  val mock = RDFMock

  @Before
  def before {
    mock.start()
    mock.load()
  }

  @After
  def after {
    mock.stop()
  }

  @Test
  def dummy_test {

    val tuples = SPARQL(mock.repo).queryTuple("""
      SELECT * WHERE { 
      
        <http://example.org/vocab/show/218> <http://example.org/vocab/show/localName> ?v_string_lang .
      
        <http://somecountry.example/census2007> <http://example.org/statsisLandlocked> ?v_bool .
        
        <http://en.wikipedia.org/wiki/Helium> <http://example.org/elementsatomicMass> ?v_decimal .
        
        <http://en.wikipedia.org/wiki/Helium> <http://example.org/elementsatomicNumber> ?v_integer .
        
        <http://en.wikipedia.org/wiki/Helium> <http://example.org/elementsspecificGravity> ?v_double .
        
        <http://www.w3.org/TR/rdf-syntax-grammar> <http://example.org/stuff/1.0/editor> ?v_bnode .
        
        <http://one.example/subject1> <http://one.example/predicate1> ?v_iri .
        
        _:user2 <http://xmlns.com/foaf/0.1/phone> ?v_foaf_phone .
        _:user2 <http://xmlns.com/foaf/0.1/mbox> ?v_foaf_mbox .
        
        _:issue7 <http://example.org/reportedOn> ?v_datetime ;
        
      }
    """).toList

    tuples.foreach { tuple =>

      val v_string_lang = tuple.getOrElse("v_string_lang", "")
      println("::STRING:: " + v_string_lang.getClass())
      println(v_string_lang)
      println()

      val v_bool = tuple.getOrElse("v_bool", "")
      println("::BOOL:: " + v_bool.getClass())
      println(v_bool)
      println()

      val v_decimal = tuple.getOrElse("v_decimal", "")
      println("::DECIMAL:: " + v_decimal.getClass())
      println(v_decimal)
      println()

      val v_bnode = tuple.getOrElse("v_bnode", "")
      println("::BNODE:: " + v_bnode.getClass())
      println(v_bnode)
      println()

      val v_iri = tuple.getOrElse("v_iri", "")
      println("::IRI:: " + v_iri.getClass())
      println(v_iri)
      println()

      val v_foaf_phone = tuple.getOrElse("v_foaf_phone", "")
      println("::v_foaf_phone:: " + v_foaf_phone.getClass())
      println(v_foaf_phone)
      println()

      val v_foaf_mbox = tuple.getOrElse("v_foaf_mbox", "")
      println("::v_foaf_mbox:: " + v_foaf_mbox.getClass())
      println(v_foaf_mbox)
      println()

      val v_datetime = tuple.getOrElse("v_datetime", "")
      println("::v_datetime:: " + v_datetime.getClass())
      println(v_datetime)
      println()

    }

  }

}

object RDFMock {

  val rdf_source = Paths.get("src/main/scala/it/almawave/kb/sparqlnew/example.ttl").toAbsolutePath().normalize().toFile()
  val repo = new SailRepository(new MemoryStore)

  def start() {
    if (!repo.isInitialized()) repo.initialize()
  }

  def stop() {
    if (repo.isInitialized()) {
      this.clear()
      repo.shutDown()
    }
  }

  def load() {

    val conn = repo.getConnection
    val rdf_format: RDFFormat = Rio.getParserFormatForFileName(rdf_source.toString()).get
    conn.add(rdf_source, "", rdf_format)

    val size = conn.size()
    println(s"loaded ${size} triples")
    conn.close()

  }

  def clear() {
    val conn = repo.getConnection
    conn.clear()
    conn.clearNamespaces()
    conn.close()
  }

}

//class org.eclipse.rdf4j.sail.memory.model.MemBNode
//class org.eclipse.rdf4j.sail.memory.model.MemLiteral
//class org.eclipse.rdf4j.sail.memory.model.MemBNode
//class org.eclipse.rdf4j.sail.memory.model.MemIRI
//class org.eclipse.rdf4j.sail.memory.model.MemLiteral
//class org.eclipse.rdf4j.sail.memory.model.DecimalMemLiteral
//class org.eclipse.rdf4j.sail.memory.model.IntegerMemLiteral
//class org.eclipse.rdf4j.sail.memory.model.NumericMemLiteral
