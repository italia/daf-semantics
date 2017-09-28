package mockup

import com.typesafe.config.ConfigFactory

object MainConfigKeysTest extends App {

  val conf = ConfigFactory.parseString("""
    
  Istat-Classificazione-08-Territorio {
  
    vocabulary.name: "Istat-Classificazione-08-Territorio"
  	
  	vocabulary.ontology.name: "CLV-AP_IT"
  	vocabulary.ontology.prefix: "clvapit"
    
    vocabulary.file: "./dist/data/vocabularies/Istat-Classificazione-08-Territorio.ttl"
    # mime: "text/turtle"
    vocabulary.contexts: [ "http://dati.gov.it/onto/clvapit#" ]
        
    vocabulary.query.csv: "./dist/data/vocabularies/Istat-Classificazione-08-Territorio#dataset.csv.sparql"
  
  }  
    
  """)
  
  val keys = conf.root().keySet()
  
  println("#### KEYS")
  println(keys)

}