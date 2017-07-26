package it.almawave.kb.old

import java.io.ByteArrayInputStream
import java.net.URI
import it.almawave.kb.repo.RDFRepository

object MainRDFRepository extends App {

  val repo = RDFRepository.memory()
  repo.start()

  val baseURI = new URI("http://testing/graph-01")
  val bais = new ByteArrayInputStream("""
      <http://testing/subject-01> <http://testing/predicate-01> <http://testing/object-01>
    """.getBytes)

  repo.helper.importFrom("ontologies")

  //  repo.store.addRDF(bais, baseURI, "N-TRIPLES", "http://testing/graph-01")

  // TODO: verify how to clean all

  //  println(repo.store.size("http://testing/graph-01"))

  //  repo.store.graphs()

  repo.prefixes.add(("foaf", "http://xmlns.com/foaf/spec/index.rdf"))

  val namespaces = repo.prefixes.list()

  println("#### NAMESPACES")
  println(namespaces.mkString("\n"))

  repo.stop()

}