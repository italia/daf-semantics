package check.rdf4j.ws
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws._
import play.api.libs.ws.ahc.AhcWSClient

object MainWSRequest extends App {

  import scala.concurrent.Future

  import scala.concurrent.ExecutionContext.Implicits._

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val ws = AhcWSClient()

  ws.url("http://localhost:8000/stanbol/ontonethub/ontologies").get()
    .map { response =>
      val status = response.status
      println(s"STATUS: ${status}")
      println(response.body)
      status
    }
    .andThen { case _ => ws.close() }
    .andThen { case _ => system.terminate() }

}