package it.almawave.daf.standardization.refactoring

/**
 * this object can be used to retrieve the queries used to recostruct hierarchies and elements details.
 * NOTE: some rework on the query was done in order to obtain a standard worflow.
 * TODO: parametrization of queries
 * TODO: externalization of queries, again (file)
 */
object QueryStandardization {

  /*
   * TODO: externalization of queries, parameters injection
   */
  def q_details(
    uri:        String,
    properties: Seq[String] = List("skos:notation", "skos:prefLabel"))(lang: String = "it") = {
    s"""
        PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
        SELECT DISTINCT *  
        WHERE {
          ?instance_uri a ?concept_uri . 
          OPTIONAL { ?concept_uri a owl:Class . }
          FILTER(?instance_uri = <${uri}>)
          ?instance_uri ?property_uri ?property_value .
          FILTER(?property_uri IN(${properties.mkString(", ")}))
          BIND(DATATYPE(?property_value) AS ?property_type)
          BIND(LANG(?property_value) AS ?property_lang) .
          FILTER(?property_lang="${lang}" || ?property_lang="")
        }
      """
  }

  /*
   * TODO:
   * - parameters for prefixes
   * - parameters for concepts
   * - parameters for hierarchy relations
   * 
   * ASK: how to handle the case without parents at all?
   */
  def q_instances(concepts: Seq[String] = List("skos:Concept")) = {
    s"""
    PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
    SELECT DISTINCT ?instance_uri ?parent_uri ?concept_ancestor
    WHERE {
      ?instance_uri a ?concept_uri .
      OPTIONAL { ?concept_uri rdfs:subclassOf* ?concept_ancestor . } 
      FILTER(?concept_uri IN (${concepts.mkString(", ")}))
      OPTIONAL { ?instance_uri skos:broader ?parent_uri . FILTER(BOUND(?parent_uri)) }
      BIND(REPLACE(STR(?instance_uri), "/", "+", "i") AS ?_uri) # hack for correct ordering
    }
    ORDER BY ?_uri 
  """
  }
}

