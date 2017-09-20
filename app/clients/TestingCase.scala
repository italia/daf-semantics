package clients

/**
 * IDEA: find a way to easily convert between different case clsses qith the same arguments signature
 */
object TestingCase extends App {

  case class UNO(id: Int, name: String)
  case class DUE(id: Int, name: String)

  val uno = UNO(1, "primo")
  println("UNO", uno, UNO.unapply(uno))

  implicit class SameWrapper[FROM](element: FROM) {

    def sameAs() = {

      DUE.tupled(UNO.unapply(uno).get)

    }
  }

  println(UNO.unapply(uno))

  val due = uno.sameAs

  println("DUE", due)

}