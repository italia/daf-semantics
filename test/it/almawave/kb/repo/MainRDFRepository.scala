package it.almawave.kb.repo

// a simple main as an example
object MainRDFRepository extends App {

  val repo = RDFRepository.memory()
  repo.start()

  repo.helper.importFrom("dist/data/ontologies")

  val results = repo.sparql.query("SELECT * WHERE { ?subject a ?concept }")

  results
    .toStream
    .foreach {
      item =>
        println(item)
    }

  repo.stop()
}