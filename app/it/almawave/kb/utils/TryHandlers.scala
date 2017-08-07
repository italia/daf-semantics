package it.almawave.kb.utils

import scala.util.Try
import scala.util.Failure
import scala.util.Success
import org.slf4j.Logger
import org.eclipse.rdf4j.repository.RepositoryConnection
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.eclipse.rdf4j.repository.Repository

object TryHandlers {

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
   * if a logger is already used in the caller context, it will be used
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

  /*
   * this could be useful for simplifying code: 
   * 	+ default connection handling (open/close)
   * 	+ default transaction handling
   */
  object RepositoryAction {

    def apply[R](repo: Repository)(conn_action: (RepositoryConnection => Any))(msg_err: String)(implicit logger: Logger) = {

      // NOTE: we could imagine using a connection pool here
      val _conn = repo.getConnection

      _conn.begin()

      val results: Try[R] = try {

        val success = Success(conn_action(_conn))
        _conn.commit()
        success.asInstanceOf[Try[R]]

      } catch {

        case ex: Throwable =>
          val failure = Failure(ex)
          _conn.rollback()
          logger.info(msg_err)
          failure

      }

      _conn.close()

      results
    }

  }

}