package it.almawave.kb.sparqlnew

import org.eclipse.rdf4j.repository.Repository
import scala.collection.mutable.ListBuffer
import org.eclipse.rdf4j.query.BindingSet
import org.eclipse.rdf4j.query.QueryLanguage
import org.eclipse.rdf4j.model.impl.SimpleLiteral
import org.eclipse.rdf4j.model.impl.DecimalLiteral
import org.eclipse.rdf4j.model.impl.IntegerLiteral
import org.eclipse.rdf4j.model.impl.NumericLiteral
import org.eclipse.rdf4j.model.impl.BooleanLiteral
import java.net.URI
import org.eclipse.rdf4j.model.Literal
import org.eclipse.rdf4j.model.Value
import org.eclipse.rdf4j.model.Statement
import org.eclipse.rdf4j.query.TupleQueryResultHandler
import org.eclipse.rdf4j.model.BNode
import org.eclipse.rdf4j.model.IRI
import java.text.SimpleDateFormat

object SPARQL {
  def apply(repo: Repository) = new SPARQL(repo)
}

/**
 * TODO: refactoring as a facade for extracting results into
 * + tuples: `Seq[Map[String, Any]]`
 * + graphs: `Seq[Map[String, Map[String, ...]]]`
 *
 * TODO: merge with managers/sparql
 *
 * should we assume that the repository is accessible (already initialized and not shutdown?)
 *
 */
class SPARQL(repo: Repository) {

  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters._

  def start() {
    if (!repo.isInitialized()) repo.initialize()
  }

  def stop() {
    if (repo.isInitialized()) repo.shutDown()
  }

  def ask(query: String): Boolean = {
    val conn = repo.getConnection
    val result = conn.prepareBooleanQuery(QueryLanguage.SPARQL, query).evaluate()
    conn.close()
    result
  }

  def query(query: String): Seq[Map[String, Any]] = {

    // TODO: handle UPDATE by Exception

    if (query.contains("SELECT ") || query.contains("select ")) {
      queryTuple(query)
    } else if (query.contains("CONSTRUCT ") || query.contains("construct ") || query.contains("DESCRIBE ") || query.contains("describe ")) {
      queryGraph(query)
    } else {
      throw new RuntimeException("cannot handle this type of query")
    }

  }

  // TODO
  def queryGraph(query: String): Seq[Map[String, Any]] = {
    throw new RuntimeException("GRAPH QUERY Not implemented yet!")
  }

  // REFACTORIZE
  def queryTuple(query: String, framing: Boolean = true): Seq[Map[String, Any]] = {

    if (!repo.isInitialized()) repo.initialize() // VERIFY if needed
    val conn = repo.getConnection

    val results = new ListBuffer[BindingSet]
    val tuples = conn.prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate()
    val binding_names = tuples.getBindingNames.toSet
    while (tuples.hasNext()) {
      val tuple: BindingSet = tuples.next()
      results += tuple
    }

    conn.close()

    // projection of the results in terms of a stream

    val list = results.toStream
      .map { _.map { el => (el.getName, parse_value(el.getValue)) }.toMap } // TODO: avoid String using typed values!

    list
  }

  def parse_value(value: Value) = {

    value match {

      case bool: BNode =>
        URI.create(s"bnode://${bool.getID}")

      case uri: IRI =>
        URI.create(uri.stringValue())

      case bool: BooleanLiteral => bool.booleanValue()

      case num: IntegerLiteral  => num.integerValue()
      case num: NumericLiteral  => num.doubleValue()
      case num: DecimalLiteral  => num.decimalValue()

      case literal: SimpleLiteral =>

        val rdf_datatype = literal.getDatatype

        val rdf_lang = if (literal.getLanguage.isPresent()) Some(literal.getLanguage.get) else None

        val rdf_value = rdf_datatype.toString() match {

          case "http://www.w3.org/2001/XMLSchema#boolean" => literal.booleanValue()

          case "http://www.w3.org/2001/XMLSchema#integer" => literal.integerValue()
          case "http://www.w3.org/2001/XMLSchema#double"  => literal.doubleValue()
          case "http://www.w3.org/2001/XMLSchema#decimal" => literal.decimalValue()

          case "http://www.w3.org/2001/XMLSchema#string"  => literal.stringValue()

          // SEE: XMLDatatypeUtil

          case "http://www.w3.org/2001/XMLSchema#dateTime" =>
            RDFDateTime(literal.calendarValue().toString())

          case "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString" =>
            StringLang(literal.getLabel, rdf_lang)

          case _ => literal.stringValue()

        }

        rdf_value

      case _ => value.toString()

    }

  }

  case class RDFDateTime(value: String)

  case class StringLang(value: String, lang: Option[String]) {
    override def toString = s""""${value}"[lang=${lang.getOrElse("")}]"""
  }

  val _handler = new TupleQueryResultHandler {

    val results = new ListBuffer[Object]

    def startQueryResult(namespaces: java.util.List[String]) {}
    def handleBoolean(bool: Boolean) {}
    def endQueryResult() {}
    def handleLinks(links: java.util.List[String]) {}
    def handleSolution(bindingSet: BindingSet) {
      println(bindingSet)
    }

  }

  object RDF4JAdapters {

    def toStatementsList(bs: BindingSet): Seq[Statement] = {

      bs.foreach { binding =>

        binding.getName

      }

      //      bs.getBindingNames.foreach { name =>
      //        bs.getBinding(name)
      //      }

      null
    }

  }

  ///// REMOVE..................

  case class StringLiteral(label: String, language: String)
  case class RDFLiteral(label: String, datatype: URI, language: String, klass: Class[_])

  def no_parse_literal(literal: Literal): Any = {

    // VERIFY

    //      BooleanLiteral,
    //      CalendarLiteral,
    //      DecimalLiteral, NumericLiteral
    //      SimpleLiteral
    //      IntegerLiteral,

    literal match {

      case bool: BooleanLiteral => bool.booleanValue()

      case num: DecimalLiteral  => num.doubleValue()
      case num: NumericLiteral  => num.doubleValue()
      case num: IntegerLiteral  => num.integerValue()

      case txt: SimpleLiteral =>

        val _type = txt.getDatatype
        val _lang = txt.getLanguage.orElse("")

        if (_type.equals(""))
          StringLiteral(txt.getLabel, _lang)
        else if (_type.equals("http://www.w3.org/2001/XMLSchema#decimal"))
          txt.decimalValue()
        else
          txt.getLabel

      case _ =>
        RDFLiteral(
          literal.getLabel,
          new URI(literal.getDatatype.stringValue()),
          literal.getLanguage.orElse(""),
          literal.getClass)
    }

  }

}
