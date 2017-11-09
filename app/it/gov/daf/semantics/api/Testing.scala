package it.gov.daf.semantics.api

import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.eclipse.rdf4j.query.QueryLanguage
import it.almawave.linkeddata.kb.repo.RDFRepository

object Testing extends App {

  val repo = RDFRepository.memory()

  repo.start()
//  repo.io.addRDFFile(new File, mimeFormat, contexts)
  
  def query = """SELECT DISTINCT * WHERE { ?s ?p ?o } """

  
  

  repo.stop()

  // ------------------------------------------------------------------

  def query2 = """
  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
  PREFIX owl: <http://www.w3.org/2002/07/owl#>
  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
  PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
  PREFIX clvapit: <http://dati.gov.it/onto/clvapit#> 
  PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
  PREFIX l0: <http://dati.gov.it/onto/l0/> 
  PREFIX poiapit: <http://dati.gov.it/onto/poiapit#> 
  
  SELECT ?ontology ?id ?uri ?label ?rank 
  
  FROM <http://dati.gov.it/onto/poiapit#> 
  
  WHERE {
  
  	?uri a skos:Concept . #poiapit:PointOfInterestCategory
    	?uri a ?concept .
    	?uri skos:prefLabel ?label .
    	?uri clvapit:hasRankOrder ?rank .
  
    	?uri ?property [] .
  	?property l0:controlledVocabulary ?vocabulary .
    	?uri a ?concept . ?concept rdfs:isDefinedBy ?ontology .
    
    	BIND(REPLACE(STR(?uri), '^.*/(.*)[#/].*$', '$1', 'i') AS ?id) #TODO:Concept-id
      
  	# parameters!
  	FILTER(LANG(?label) = 'it')
  }
  
  ORDER BY ?rank 
  """

}