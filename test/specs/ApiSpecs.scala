package specs

import org.specs2.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import play.api.test.WithServer

import play.core.server.Server
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.Application

@RunWith(classOf[JUnitRunner])
class ApiSpecs extends Specification {

  def application: Application = GuiceApplicationBuilder().build()

  var port = 0
  Server.withApplication(application) {
    p => port = p.value
  }

  println(port)

  def is = s2"""
  Checking swagger API...
  
  runs on port port
  
  contain 11 characters                             $e1
  
  """

  def e1 = "localhost" must be equalTo "localhost"

}