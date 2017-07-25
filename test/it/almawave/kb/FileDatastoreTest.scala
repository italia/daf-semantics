package it.almawave.kb

import org.junit.Assert
import org.junit.Test

class FileDatastoreTest {

  @Test
  def test_local_relative_files {
    var fs = new FileDatastore("ontologies")
    Assert.assertTrue(fs.list("owl", "ttl").size > 0)
  }

  //  @Test DISABLED
  def test_remote_files {
    var fs = new FileDatastore("http://xmlns.com/foaf/spec/index.rdf")
    Assert.assertTrue(fs.list("rdf").size > 0)
  }

}