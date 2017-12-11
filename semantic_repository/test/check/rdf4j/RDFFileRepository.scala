package check.rdf4j

import java.net.URL
import org.eclipse.rdf4j.sail.SailConnection
import org.eclipse.rdf4j.sail.helpers.AbstractSail
import org.eclipse.rdf4j.sail.Sail
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.eclipse.rdf4j.query.QueryLanguage
import scala.collection.mutable.ListBuffer

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.rio.Rio
import org.eclipse.rdf4j.query.BindingSet

object RDFFileRepository extends App {

  val onto_url = "https://raw.githubusercontent.com/italia/daf-ontologie-vocabolari-controllati/master/Ontologie/IndirizziLuoghi/latest/CLV-AP_IT.ttl"

  SPARQL.query(
    "http://dati.gov.it/onto/CLV-AP_IT/",
    """SELECT * WHERE { GRAPH ?graph { ?uri ?prop ?obj . } }""",
    onto_url)

}

object SPARQL {

  def query(baseURI: String, query: String, urls: String*) = {
    val repo = new SailRepository(new MemoryStore)
    repo.initialize()
    val conn = repo.getConnection
    val vf = conn.getValueFactory

    urls.foreach { url =>
      val format = Rio.getParserFormatForFileName(url).get
      conn.add(new URL(url), url, format, vf.createIRI(baseURI), vf.createIRI(url))
    }

    val results = new ListBuffer[BindingSet]
    val tuples = conn.prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate()
    while (tuples.hasNext())
      results += tuples.next()

    println(results.mkString("\n"))
    conn.close()
    repo.shutDown()

  }

}