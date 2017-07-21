package it.almawave.kb

import java.io.ByteArrayInputStream
import java.net.URI

object MainRDFRepository extends App {

  val repo = RDFRepository.memory()
  repo.start()

  val baseURI = new URI("http://testing/graph-01")
  val bais = new ByteArrayInputStream("""
      <http://testing/subject-01> <http://testing/predicate-01> <http://testing/object-01>
    """.getBytes)

  repo.addRDF(bais, baseURI, "N-TRIPLES", "http://testing/graph-01")

  // TODO: verify how to clean all

  repo.graphs()

  repo.stop()

}