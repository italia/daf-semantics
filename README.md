
semantic_standardization
==========================


## example: retrieving a vocabulary dataset

We can obtain a de-normalized, tabular version of the vocabulary `Istat-Classificazione-08-Territorio` using the curl call:
```
$ curl -X GET "http://localhost:9000/kb/v1/vocabularies/Istat-Classificazione-08-Territorio?lang=it" -H  "accept: application/json" -H  "content-type: application/json"
```
this will return a result structure similar to the following one:

```
[
  [
	{ "key": "CLV-AP_IT_Country_name", "value": "Italia"},
	{"key": "CLV-AP_IT_City_name", "value": "Abano Terme"},
	{"key": "CLV-AP_IT_Province_name", "value": "Padova"},
	{"key": "CLV-AP_IT_Region_name", "value": "Veneto"}
  ],
  [
	{"key":"CLV-AP_IT_Province_name", "value": "Lodi"},
	{"key":"CLV-AP_IT_City_name", "value": "Abbadia Cerreto"},
	{"key": "CLV-AP_IT_Country_name", "value": "Italia"},
	{"key": "CLV-AP_IT_Region_name", "value": "Lombardia"}
  ]
  ...
]
```

## example: retrieve th hierarchies for the properties used 

If we have the example vocabulary `Istat-Classificazione-08-Territorio`, which uses terms from the ontology `clvapit`, we can retrieve the local hierarchy associated to each property with the curl command:

```
$ curl -X GET http://localhost:9000/kb/v1/hierarchies/properties?vocabulary_name=Istat-Classificazione-08-Territorio&ontology_name=clvapit&lang=it -H  "accept: application/json" -H  "content-type: application/json"
```

which will return the results:

```
[
  {
    "vocabulary": "CLV-AP_IT",
    "path": "CLV-AP_IT.Country.name",
    "hierarchy_flat": "Country",
    "hierarchy": "hierarchy"
  },
  {
    "vocabulary": "CLV-AP_IT",
    "path": "CLV-AP_IT.City.name",
    "hierarchy_flat": "Country.Region.Province.City",
    "hierarchy": "hierarchy"
  }
  ...
]
```


## example configurations

An example configuration for working with a vocabulary (VocabularyAPI):

```
"data_dir": "./data"

"Istat-Classificazione-08-Territorio" {

	vocabulary.name: "Istat-Classificazione-08-Territorio"
	
	vocabulary.ontology.name: "CLV-AP_IT"
	
	vocabulary.ontology.prefix: "clvapit"
	
	vocabulary.file: ${data_dir}"/vocabularies/Istat-Classificazione-08-Territorio.ttl"
	
	vocabulary.contexts: [ "http://dati.gov.it/onto/clvapit#" ]
	
	vocabulary.query.csv: ${data_dir}"/vocabularies/Istat-Classificazione-08-Territorio#dataset.csv.sparql"
	
}
```

The `vocabulary.query.csv` is a reference to a SPARQL query designed to produce a flat representation of the vocabulary informations.


An example configuration for working with an ontology (OntologyAPI) could be similar to the following one:

```
clvapit {
	
	ontology.name: "CLV-AP_IT"
	ontology.prefix: "clvapit"
	
	ontology.file: ${data_dir}"/ontologies/agid/CLV-AP_IT/CLV-AP_IT.ttl"
	
	ontology.contexts: [ "http://dati.gov.it/onto/clvapit#" ]
	
	ontology.query.hierarchy: ${data_dir}"/ontologies/agid/CLV-AP_IT/CLV-AP_IT.hierarchy.sparql"
}
```
The `ontology.query.hierarchy` is a reference to a SPARQL query designed to produce a flat representation of the vocabulary informations.


* * * 

**Note** that the `${data_dir}` can be replaced with a specific root path on disk: at this stage of the development this will be a relative folder (for example: `/dist/data` for the sbt project).

Eventually the idea of pre-loading ontologies and vocabularies from disk can be replaced with the import from a central datastore (dedicated maintain the last version of ontologies), where they are already loaded under conventional paths/names. This way we will be able to switch from an in-memory tiny repository (one for each ontology/vocabulary) to a central RDF/SPARQL repository, containing all the pre-loaded ontologies and vocabulariesl. 


----

TODO:

+ more documentation / comments
+ more proper tests
+ remove redundant classes for RDFREpository, importing external kb-core dependency, instead