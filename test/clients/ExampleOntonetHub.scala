package clients

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ExampleOntonetHub extends App {

  import scala.concurrent.ExecutionContext.Implicits._

  val http = HTTPClient

  http.start()

  val ws = http.ws

  val future = ws.url("http://localhost:8000/stanbol/ontonethub/ontologies/find")
    .withHeaders(("accept", "application/json"))
    .withHeaders(("content-type", "application/x-www-form-urlencoded"))
    .post(s"name=pers")
    .map { res => res.body }

  val results = Await.result(future, Duration.Inf)
  println(results)

  http.stop()

}