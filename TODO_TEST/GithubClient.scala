package examples.github

import javax.inject.Inject
import play.api.libs.ws.WSClient
import scala.concurrent.{ ExecutionContext, Future }
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import play.api.libs.json.JsValue.jsValueToJsLookup

@RunWith(classOf[JUnitRunner])
class GitHubClient(ws: WSClient, baseUrl: String)(implicit ec: ExecutionContext) {
  @Inject def this(ws: WSClient, ec: ExecutionContext) = this(ws, "https://api.github.com")(ec)

  def repositories(): Future[Seq[String]] = {
    ws.url(baseUrl + "/repositories").get().map { response =>
      (response.json \\ "full_name").map(_.as[String])
    }
  }

}