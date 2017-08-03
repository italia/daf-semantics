package it.almawave.kb.utils

import scala.util.control.NonFatal
import scala.util.{ Failure, Success, Try }
import play.Logger

/**
 * NOTE: this object is here just as a reference, the file will be dropped soon
 *
 * IDEA for refactorizing results, handling exceptions/failures in a more idiomatic way
 *
 * SEE: https://codereview.stackexchange.com/questions/79267/scala-trywith-that-closes-resources-automatically
 */
@Deprecated
object TryWith {

  val logger = Logger.underlying()

  def apply[C <: AutoCloseable, R](input: => C)(closeable: AutoCloseable => R): Try[R] =
    Try { input }
      .flatMap {
        item =>
          try {
            Success(closeable(item))
          } catch {
            case NonFatal(ex) => Failure(ex)
          } finally {
            try {
              item.close()
            } catch {
              case ex: Exception =>
                logger.error(s"Failed to close Resource: ${ex}")
            }
          }
      }

}

