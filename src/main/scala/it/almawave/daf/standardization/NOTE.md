 
## vocabularies-api

vocabularies-api endpoint uses the following data structure
{
	"uri": "http://dati.gov.it/onto/controlledvocabulary/PublicEvent/16_DanceShow",
	"notation": "1.6",
	"label": "Spettacolo di Danza",
	"rank": "2",
	"parent_uri": "http://dati.gov.it/onto/controlledvocabulary/PublicEvent/1_CulturalEvent",
	"scheme": "http://dati.gov.it/onto/controlledvocabulary/PublicEvent"
}

while the standardization currently exposes this other structure
{
	uri: "http://dati.gov.it/onto/controlledvocabulary/POICategoryClassification/cat_5_automotive",
	name: "label_level1",
	value: "Settore automobilistico",
	datatype: "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString",
	meta_level1: "SKOS.Concept.prefLabel",
	meta_level2: "POICategoryClassification.level_1"
}

we could imagine to re-use the same query in order to expose the flat hierarchy




