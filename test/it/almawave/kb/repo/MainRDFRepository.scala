package it.almawave.kb.repo

object MainRDFRepository extends App {

  // TODO: add sparql endpoint

  val repo = RDFRepository.memory()
  repo.start()

  repo.helper.importFrom("dist/data/ontologies")

  val results = repo.sparql.query("SELECT * WHERE { ?subject a ?concept }")

  results.foreach {
    item =>
      println(item)
  }

  repo.stop()
}