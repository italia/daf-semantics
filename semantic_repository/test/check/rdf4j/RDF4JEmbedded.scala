package check.rdf4j

import org.eclipse.rdf4j.repository.Repository
import scala.concurrent.Future
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import scala.util.Try
import org.eclipse.rdf4j.query.QueryLanguage
import org.eclipse.rdf4j.query.TupleQueryResultHandler
import scala.collection.mutable.ListBuffer
import org.eclipse.rdf4j.query.BindingSet
import org.eclipse.rdf4j.rio.RDFFormat
import java.io.File
import org.eclipse.rdf4j.model.Statement
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.eclipse.rdf4j.model.Value
import org.eclipse.rdf4j.model.impl.SimpleIRI
import org.eclipse.rdf4j.sail.memory.model.MemIRI
import org.eclipse.rdf4j.sail.memory.model.MemLiteral
import org.eclipse.rdf4j.model.IRI
import org.eclipse.rdf4j.model.URI
import org.mapdb.BTreeMap.BNode
import org.eclipse.rdf4j.model.Literal
import scala.collection.immutable.Stream
import org.eclipse.rdf4j.common.iteration.Iterations
import org.eclipse.rdf4j.rio.Rio
import java.io.FileInputStream

// NOTE: this is only an example, and will be deleted soon
object MainEndpoint extends App {

  val json_mapper = new ObjectMapper
  json_mapper.registerModule(DefaultScalaModule)
  val json_reader = json_mapper.reader()
  val json_writer = json_mapper.writerWithDefaultPrettyPrinter()

  val repo = RDF4J.memory
  repo.start()
  repo.init()

  // TESTING TUPLES
  //    val results = repo.query_tuples("SELECT DISTINCT ?concept ?s WHERE { ?s a ?concept }")
  //    println(results.get.mkString("\n"))

  // TESTING GRAPHS
  val results = repo.query_graph("""
    PREFIX ex: <http://example.org/>
    DESCRIBE ?s
    # CONSTRUCT { ?s a ?concept } 
    WHERE { ?s a ?concept }
  """)
  //  println(results.get.mkString("\n"))

  val tree = repo.framing(results.get)
  //  println(tree.mkString("\n"))

  val json = json_writer.writeValueAsString(tree)
  println(json)

  repo.stop()

}

object RDF4J {

  def memory = new RDF4J(new SailRepository(new MemoryStore))

}

class RDF4J(repo: Repository) {

  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters._

  import scala.concurrent.ExecutionContext.Implicits._

  def init() {

    val conn = repo.getConnection

    val data = "C:/Users/Al.Serafini/repos/datasets/DATA/ANPR/RDF/anpr_comuni.dump.ttl"

    conn.add(new File(data), "", RDFFormat.TURTLE)
    //    conn.add(new File("./ontologies/agid/DCAT-AP_IT/DCAT-AP_IT.owl"), "", RDFFormat.RDFXML)

    conn.close()

  }

  def prefixes = {
    val conn = repo.getConnection
    val _prefixes = Iterations.asList(conn.getNamespaces)
      .map { ns =>
        (ns.getPrefix, ns.getName)
      }.toMap
    conn.close()
    _prefixes
  }

  def start() = Try {

    if (!repo.isInitialized())
      repo.initialize()

  }

  def stop() = Try {

    if (repo.isInitialized())
      repo.shutDown()

  }

  def query_tuples(query: String): Try[Stream[Map[String, Object]]] = Try {

    val conn = repo.getConnection

    val results = new ListBuffer[BindingSet]
    val tuples = conn.prepareTupleQuery(QueryLanguage.SPARQL, query)
      .evaluate()
    while (tuples.hasNext())
      results += tuples.next()

    conn.close()

    results.map { bs =>
      bs.getBindingNames.toList
        .map { name => bs.getBinding(name) }
        .map { bs => (bs.getName, bs.getValue) }.toMap
    }.toStream

  }

  def query_graph(query: String): Try[Stream[Statement]] = Try {

    val conn = repo.getConnection

    val results = new ListBuffer[Statement]
    val tuples = conn.prepareGraphQuery(QueryLanguage.SPARQL, query)
      .evaluate()

    while (tuples.hasNext())
      results += tuples.next()

    conn.close()

    results.toStream

  }

  def framing(statements: Stream[Statement]) = {

    def curie(uri: String) = "ex:" + uri.replaceAll(".*[/#](.*)", "$1")

    val list1 = statements.map { st =>
      (st.getSubject, st.getPredicate, st.getObject, st.getContext)
    }
      .groupBy(_._1)
      .map { st =>
        val sub = curie(st._1.toString())
        val prps = st._2
          .groupBy(_._2)
          .map { st =>
            val prp = curie(st._1.toString())
            // _._3
            val objs = st._2.map { st =>

              st._3 match {

                case uri: URI =>
                  curie(uri.stringValue())

                case literal: Literal =>
                  //                  val dt = literal.getDatatype.getLocalName
                  //                  println("DATATYPE LITERAL: " + dt)
                  literal.stringValue()

                case x =>
                  x.stringValue()

              }

            }.toSet
            (prp, objs)
          }.toMap
        (sub, prps)
      }.toMap

    list1

  }

}
