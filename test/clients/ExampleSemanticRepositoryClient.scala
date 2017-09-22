package clients

import java.io.File
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import clients.HTTPClient

object ExampleSemanticRepositoryClient extends App {

  import scala.concurrent.ExecutionContext.Implicits._

  val http = HTTPClient
  http.start()
  val ws = http.ws

  val fileName = "FOAF_42"
  val rdfDocument = new File("dist/data/ontologies/foaf/foaf.rdf")
  val prefix = "FOAF_42"
  val context = "http://example/FOAF_42"

  val sc = SemanticRepositoryClient.create(ws)

  val future = sc.crud
    .add_ontology(fileName, rdfDocument, prefix, context)

  val results = Await.result(future, Duration.Inf)
  println(results)

  // ---- testing forwarding --------------------------------
//  val response 
    
    
  // ---- testing forwarding --------------------------------

  http.stop()
}