package clients

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws.ahc.AhcWSClient

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

}
