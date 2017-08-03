package it.almawave.kb.repo

// a simple main as an example
object MainRDFRepository extends App {

  val repo = RDFRepository.memory()
  repo.start()

  repo.io.importFrom("dist/data/ontologies")

  val results = repo.sparql.query("SELECT * WHERE { ?subject a ?concept }")

  results
    .get
    .toStream
    .foreach {
      item =>
        println(item)
    }

  repo.stop()
}