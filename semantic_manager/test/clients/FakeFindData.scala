package clients

import com.fasterxml.jackson.databind.ObjectMapper
import scala.concurrent.Future

import utilities.Adapters
import utilities.Adapters._

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import java.lang.Float
import scala.math.Ordering.FloatOrdering
import review.ontonethub.OntonethubFindParser

object MainFakeFind extends App {

  val data = FakeOntologyHubFind.request.await

  val results = OntonethubFindParser.parse(data)

  println(results.mkString("\n"))
}

object FakeOntologyHubFind {

  import scala.concurrent.ExecutionContext.Implicits._

  def request = Future {
    data
  }

  val data = """
  {
	"query": {
		"selected": ["http:\/\/www.w3.org\/2000\/01\/rdf-schema#label", "http:\/\/stanbol.apache.org\/ontology\/entityhub\/query#score"],
		"constraints": [{
			"type": "text",
			"patternType": "wildcard",
			"text": "name",
			"proximityRanking": false,
			"field": "http:\/\/www.w3.org\/2000\/01\/rdf-schema#label",
			"boost": 1
		}],
		"limit": 100,
		"offset": 0
	},
	"results": [{
		"id": "http:\/\/xmlns.com\/foaf\/0.1\/name",
		"http:\/\/www.w3.org\/2000\/01\/rdf-schema#label": [{"type": "text","value": "name"}],
		"http:\/\/stanbol.apache.org\/ontology\/entityhub\/query#score": [{"type": "value","xsd:datatype": "xsd:float","value": 10.559994}]
	}, {
		"id": "http:\/\/xmlns.com\/foaf\/0.1\/mbox_sha1sum",
		"http:\/\/www.w3.org\/2000\/01\/rdf-schema#label": [{"type": "text","value": "sha1sum of a personal mailbox URI name"}],
		"http:\/\/stanbol.apache.org\/ontology\/entityhub\/query#score": [{"type": "value","xsd:datatype": "xsd:float","value": 3.5199978}]
	}, {
		"id": "http:\/\/xmlns.com\/foaf\/0.1\/accountName",
		"http:\/\/www.w3.org\/2000\/01\/rdf-schema#label": [{
			"type": "text",
			"value": "account name"
		}],
		"http:\/\/stanbol.apache.org\/ontology\/entityhub\/query#score": [{
			"type": "value",
			"xsd:datatype": "xsd:float",
			"value": 7.0399957
		}]
	}, {
		"id": "http:\/\/xmlns.com\/foaf\/0.1\/givenName",
		"http:\/\/www.w3.org\/2000\/01\/rdf-schema#label": [{
			"type": "text",
			"value": "Given name"
		}],
		"http:\/\/stanbol.apache.org\/ontology\/entityhub\/query#score": [{
			"type": "value",
			"xsd:datatype": "xsd:float",
			"value": 7.0399957
		}]
	}, {
		"id": "http:\/\/xmlns.com\/foaf\/0.1\/givenname",
		"http:\/\/www.w3.org\/2000\/01\/rdf-schema#label": [{
			"type": "text",
			"value": "Given name"
		}],
		"http:\/\/stanbol.apache.org\/ontology\/entityhub\/query#score": [{
			"type": "value",
			"xsd:datatype": "xsd:float",
			"value": 7.0399957
		}]
	}, {
		"id": "http:\/\/xmlns.com\/foaf\/0.1\/family_name",
		"http:\/\/www.w3.org\/2000\/01\/rdf-schema#label": [{
			"type": "text",
			"value": "family_name"
		}],
		"http:\/\/stanbol.apache.org\/ontology\/entityhub\/query#score": [{
			"type": "value",
			"xsd:datatype": "xsd:float",
			"value": 5.279997
		}]
	}]
  }  
  """

}