package it.almawave.kb.utils

import scala.util.Try
import scala.util.Failure
import scala.util.Success
import org.slf4j.Logger

object TryHelpers {

  // SEE: https://stackoverflow.com/questions/20632250/how-can-i-express-finally-equivalent-for-a-scalas-try
  implicit class TryHasFinally[T](val value: Try[T]) extends AnyVal {

    import scala.util.control.NonFatal

    def Finally(action: => Unit): Try[T] =
      try {
        action
        value
      } catch {
        case NonFatal(cause) => Failure[T](cause)
      }
  }

  /**
   * this object could be used for simplyfing handling of loggin/exceptions while getting results of an operation
   */
  object TryLog {

    def apply[X](block: => X)(msg_err: String)(implicit logger: Logger): Try[X] = {

      try {

        Success { block }

      } catch {

        case ex: Throwable =>
          logger.error(s"${msg_err}\n${ex}")
          Failure(ex)

      }

    }
  }

}