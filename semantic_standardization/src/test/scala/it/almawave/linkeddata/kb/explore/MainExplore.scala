package it.almawave.linkeddata.kb.explore

import java.net.URL
import it.almawave.linkeddata.kb.file.RDFFileRepository
import org.eclipse.rdf4j.repository.Repository
import it.almawave.kb.sparqlnew.SPARQL
import org.eclipse.rdf4j.sail.memory.model.MemLiteral
import org.eclipse.rdf4j.model.Literal
import org.eclipse.rdf4j.model.IRI
import org.eclipse.rdf4j.model.BNode
import java.net.URI
import org.eclipse.rdf4j.model.impl.BooleanLiteral
import org.eclipse.rdf4j.model.impl.DecimalLiteral
import org.eclipse.rdf4j.model.impl.NumericLiteral
import org.eclipse.rdf4j.model.impl.IntegerLiteral
import org.eclipse.rdf4j.model.impl.SimpleLiteral
import it.almawave.kb.sparqlnew.Framing

/**
 * TODO:
 * 	- handle models as case classes
 * 	- convert between case classes and maps
 */
object MainExplore extends App {

  val rdf_source = new URL("file:///C:/Users/Al.Serafini/repos/DAF/semantic_standardization/ontologie-vocabolari-controllati/Ontologie/POI/latest/POI-AP_IT.ttl")

  val repo = new RDFFileRepository(rdf_source)

  val exp = new ExploreBox(repo)

  val _concepts = exp.concepts.toList

  println("\n\n##################################à")
  _concepts.foreach { uri =>

    val details = exp.details_of_concept(uri)._2.toList
    println(s"\nURI: <${uri}>")
    println(details.toList.map(x => s"\t${x._1} -> ${x._2}").mkString("\n"))

  }

}

class ExploreBox(repo: Repository) {

  import it.almawave.linkeddata.kb.utils.ModelAdapter
  //  import it.almawave.linkeddata.kb.catalog.SPARQL

  //  ModelAdapter.fromMap(map)

  def concepts: Seq[String] = {

    val fields = List("concept_uri", "klass_uri")

    SPARQL(repo).queryTuple("""
      PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
      PREFIX owl: <http://www.w3.org/2002/07/owl#>
      SELECT DISTINCT * 
      WHERE {
        
      	?concept_uri a / rdfs:subclassof* ?klass_uri .
      	FILTER(?klass_uri IN (rdfs:Class, owl:Class))
      	
      	FILTER(!isBlank(?concept_uri))
      
      }
    """)
      .map(item => item.filterKeys(x => fields.contains(x)))
      .flatten
      .map(_._2.toString())
      .distinct

  }

  def details_of_concept(uri: String) = {
    val _query = s"""
      SELECT DISTINCT * 
      WHERE {
      
        ?uri ?property_uri ?object_uri .
        FILTER(?uri = <${uri}>)
        
        OPTIONAL { ?property_uri a ?property_type . }
        
      }
    """
    //    println(s"\n\nSPARQL> ${_query}")
    val tuples: Seq[Map[String, Any]] = SPARQL(repo).queryTuple(_query)
    //    println("TUPLES.............")
    //    tuples.foreach(println)
    //    println(".............TUPLES\n")

    val flatten = tuples.map { tuple =>

      val prp = tuple.getOrElse("property_uri", "").toString()
      val obj = tuple.getOrElse("object_uri", "")
      Map(prp -> obj)

    }.toList

    (uri, Framing.mergeTuples(flatten))
  }

}
