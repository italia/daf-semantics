package it.almawave.kb

import org.eclipse.rdf4j.query.TupleQueryResult
import org.eclipse.rdf4j.query.BindingSet

object Rdf4jAdapters {

  implicit class TupleResultIterator(result: TupleQueryResult) extends Iterator[BindingSet] {
    def hasNext: Boolean = result.hasNext()
    def next(): BindingSet = result.next()
  }

}