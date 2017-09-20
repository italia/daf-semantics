package clients

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws.ahc.AhcWSClient

/**
 * This is just a facility for simplifying tests with ws locally
 */
object HTTPClient {

  var system: ActorSystem = null
  var ws: AhcWSClient = null

  def start() {

    if (system == null) {
      system = ActorSystem("HTTPClient")
    }

    //    if (ws == null)
    ws = AhcWSClient()(ActorMaterializer()(system))
  }

  def stop() {

    // close
    if (ws != null)
      ws.close()

    if (system != null)
      system.terminate()

    // force reset
    system = null
  }

  // CHECK
  //  def request(url: String) = {
  //    val request = ws
  //      .url(url)
  //    .withHeaders(hdrs)
  //    .withAuth(username, password, scheme)
  //    .withBody(body)
  //    .withFollowRedirects(follow)
  //    .withMethod(method)
  //    .withProxyServer(proxyServer)
  //    .withRequestFilter(filter)
  //    .withRequestTimeout(timeout)
  //    .withVirtualHost(vh)
  //    .withQueryString(parameters)
  //  }

}
