package it.almawave.kb

import org.eclipse.rdf4j.model.Model
import org.eclipse.rdf4j.rio.RDFFormat
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import java.io.OutputStream
import org.eclipse.rdf4j.repository.RepositoryResult
import org.eclipse.rdf4j.query.TupleQueryResult
import org.eclipse.rdf4j.query.BindingSet
import org.eclipse.rdf4j.model.impl.SimpleValueFactory

object RDFHelper {

  private val vf = SimpleValueFactory.getInstance

  def toIRIs(doc: Model, contexts: String*) =
    contexts.map { cx => vf.createIRI(cx) }

  def prettyPrint(doc: Model, out: OutputStream, format: RDFFormat = RDFFormat.TURTLE) = ???

  implicit class RepositoryResultIterator[T](result: RepositoryResult[T]) extends Iterator[T] {
    def hasNext: Boolean = result.hasNext()
    def next(): T = result.next()
  }

  implicit class TupleResultIterator(result: TupleQueryResult) extends Iterator[BindingSet] {
    def hasNext: Boolean = result.hasNext()
    def next(): BindingSet = result.next()
  }

}