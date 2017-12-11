package it.almawave.linkeddata.kb.repo.catalog

import org.junit.Before
import org.junit.After
import org.junit.Test
import java.net.URL
import org.eclipse.rdf4j.rio.Rio
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.model.util.Models
import org.eclipse.rdf4j.model.Model
import it.almawave.linkeddata.kb.repo.RDFRepository
import it.almawave.linkeddata.kb.repo.RDFRepositoryBase
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.eclipse.rdf4j.query.QueryLanguage
import scala.collection.mutable.ListBuffer
import org.junit.Assert

class TestingRDFCatalogmanager {

  val mock: RDFRepositoryBase = RDFRepository.memory()

  @Before()
  def before() {

    org.junit.Assume.assumeTrue(mock.isAlive().get)

    mock.start()
    mock.store.clear()
  }

  @After()
  def after() {
    mock.stop()
  }

  @Test
  def add_ontology() {

    val onto_id = "CLV-AP_IT"
    val onto_url = "https://raw.githubusercontent.com/italia/daf-ontologie-vocabolari-controllati/master/Ontologie/IndirizziLuoghi/latest/CLV-AP_IT.ttl"
    val onto_prefix = "clvapit"
    val onto_context = s"http://dati.gov.it/ontologies/${onto_id}/"
    val onto_mime = "text/turtle"
    val onto_base = onto_context

    val size_before = mock.store.size().get

    mock.catalog.addOntology(new URL(onto_url), onto_mime, onto_id, onto_prefix, onto_base, onto_context)

    val size_after = mock.store.size().get

    Assert.assertTrue(size_after > size_before)

    val prefixesMap = mock.prefixes.list().get
    Assert.assertTrue(prefixesMap.size > 0)
    Assert.assertTrue(prefixesMap.contains(onto_prefix))

    val contexts = mock.store.contexts().get
    Assert.assertTrue(contexts.contains(onto_context))

  }

  @Test
  def remove_ontology() {

    val test_onto = "https://raw.githubusercontent.com/italia/daf-ontologie-vocabolari-controllati/master/Ontologie/IndirizziLuoghi/latest/CLV-AP_IT.ttl"
    add_vocabulary() // TODO: refactorization here (avoiding dependent test)!!
    mock.catalog.removeOntologyByURI(test_onto)

    Assert.assertEquals(0, mock.store.size(test_onto).get)

  }

  @Test
  def add_vocabulary() {

    val voc_id = "CLV-AP_IT"
    val voc_url = "https://raw.githubusercontent.com/italia/daf-ontologie-vocabolari-controllati/master/VocabolariControllati/ClassificazioneTerritorio/Istat-Classificazione-08-Territorio.ttl"
    val voc_mime = "text/turtle"
    val voc_context = s"http://dati.gov.it/vocabularies/${voc_id}"
    val voc_base = voc_context

    mock.store.clear()

    val size_before = mock.store.size(voc_context).get

    mock.catalog.addVocabulary(new URL(voc_url), voc_mime, voc_id, voc_base, voc_context)

    val size_after = mock.store.size(voc_context).get

    Assert.assertTrue(size_after > size_before)

    val contexts = mock.store.contexts().get
    Assert.assertTrue(contexts.contains(voc_context))

  }

  @Test
  def remove_vocabulary() {

    val voc_uri = "https://raw.githubusercontent.com/italia/daf-ontologie-vocabolari-controllati/master/VocabolariControllati/ClassificazioneTerritorio/Istat-Classificazione-08-Territorio.ttl"
    val voc_id = "CLV-AP_IT"
    val voc_context = s"http://dati.gov.it/vocabularies/${voc_id}"

    add_vocabulary() // TODO: refactorization!!
    mock.catalog.removeVocabularyByURI(voc_uri)

    Assert.assertEquals(0, mock.store.size(voc_uri).get)

  }

}
