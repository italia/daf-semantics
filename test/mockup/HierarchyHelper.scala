package mockup

/**
 * drafts / experiments
 */
object HierarchyHelper {

  // TODO: refactorizations

  def filters_by_lang(lang: String, binding: String = "label") = s"""
  FILTER(LANG(?${binding})='${lang}')  
  """

  def prepare_query(lang: String = "it") = s"""
  PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
  PREFIX owl: <http://www.w3.org/2002/07/owl#>
  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
  PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
  PREFIX clvapit: <http://dati.gov.it/onto/clvapit#> 
  
  SELECT ?ontology ?id ?uri ?label ?rank
  
  FROM <http://dati.gov.it/onto/clvapit#> 
  WHERE {
  
    ?uri a owl:Class .
    ?uri rdfs:label ?label .
    ?uri clvapit:hasRankOrder ?rank .
   
    ${filters_by_lang(lang)}
    
    BIND(REPLACE(STR(?uri), 'http://dati.gov.it/onto/clvapit#(.*)', '$$1') AS ?id)
    BIND(CONCAT('clvapit') AS ?ontology)
    
  }
  ORDER BY ?rank 
  """

}