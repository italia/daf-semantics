package check.rdf4j

import it.almawave.kb.repo.RDFRepositoryBase
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.eclipse.rdf4j.sail.inferencer.fc.ForwardChainingRDFSInferencer
import org.eclipse.rdf4j.rio.RDFFormat
import java.io.StringReader
import org.eclipse.rdf4j.query.QueryLanguage

object CheckInferences extends App {

  val mem = new MemoryStore
  val repo: Repository = new SailRepository(new ForwardChainingRDFSInferencer(mem))

  repo.initialize()

  val conn = repo.getConnection

  val res = """
    
    @prefix ex: <http://example.org/> .
    
    ex:person_01 a ex:Person ;
      ex:name "first person" 
    .
    ex:person_02 a ex:Employee ;
      ex:name "second person" 
    .
    
    ex:Person a owl:Class .
    
    ex:Employee a owl:Class;
      rdfs:subClassOf ex:Person .
    
  """
  conn.add(new StringReader(res), "", RDFFormat.TURTLE)

  val query = """
    PREFIX ex: <http://example.org/> 
    SELECT *
    WHERE {
      ?subject a ?concept ;
        ex:name ?name .
      ?subject a ex:Person .
      ?concept rdfs:subClassOf* ex:Person .
    }  
  """
  
  val results = conn.prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate()

  while(results.hasNext()){
    val bs = results.next()
    println(bs)
  }
  
  conn.close()

  repo.shutDown()

}