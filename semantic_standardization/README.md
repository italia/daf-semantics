
semantic_standardization
==========================

This project is currently a POC exploring the standardization of terms using the vocabulary `Istat-Classificazione-08-Territorio` and the ontology `CLV-AP_IT`.
Currently the component are designed to use an in-memory storage of only those ontology and vocabulary, but the component can be extended to act in a similar way for different use cases and ontology/vocabulary couples. 

Two endpoints are provided:

1. the first one retrieves a flat representation of a vocabulary (conceptually similar to a CSV, but in JSON), using an ad-hoc SPARQL query.
2. the second on expose a list of properties actually used in a vocabulary from an ontology, returning the "local" hierarchy for each property. 

The idea is that each endpoint (and its configured queries) acts for a very specific domain, so the next versions could introduce new vocabularies and ontologies, but needs to create ad-hoc SPARQL queries for retrieving the informations needed.

## semantic annotation in DAF ingestion

The [DAF](https://github.com/italia/daf) `semantic_annotation` has currently the following structure: `{ontology}.{concept}.{property}`.
During the ingestion phase of datasets in DAF platform a `semantic_annotation` is used, in order to relate some column of a dataset to the most appropriate property of a given existing concept, from the controlled vocabularies.

**Note** that while the annotation is used to relate cells with vocabularies, it does not save explicitly a reference to the vocabularies used. A reference to concept from an ontology is used instead.


## examples


### example: sequence of calls

1. retrieves (vocabulary,ontology) reference from semantic_annotation tag
```
curl -X GET http://localhost:9000/kb/v1/daf/annotation/lookup?semantic_annotation=POI-AP_IT.PointOfInterestCategory.POIcategoryIdentifier -H  "accept: application/json" -H  "content-type: application/json"
```

2. retrieves the hierarchies for a given property
```
curl -X GET http://localhost:9000/kb/v1/hierarchies/properties?vocabulary_name=POICategoryClassification&ontology_name=poiapit&lang=it -H  "accept: application/json" -H  "content-type: application/json"
```

3. retrieves the dataset values for a certain vocaulary
```
curl -X GET http://localhost:9000/kb/v1/vocabularies/POICategoryClassification?lang=it -H  "accept: application/json" -H  "content-type: application/json"
```

----

### example: retrieves informations from the semantic_annotation tag
With this endpoint we can retrieve informations about the vocabulary/ontology pair related to a given `semantic_annotation` tag:

```
curl -X GET http://localhost:9000/kb/v1/daf/annotation/lookup?semantic_annotation={semantic_annotation} \ 
-H  "accept: application/json" -H  "content-type: application/json"
```

for example, for the Point Of Interest vocabulary:

```
curl -X GET 'http://localhost:9000/kb/v1/daf/annotation/lookup?semantic_annotation=POI-AP_IT.PointOfInterestCategory.POIcategoryIdentifier' \
-H  "accept: application/json" -H  "content-type: application/json"
```

This will return a datastructure similar to the following one for each tag:

```
[
  {
    "vocabulary_id": "POICategoryClassification",
    "vocabulary": "http://dati.gov.it/onto/controlledvocabulary/POICategoryClassification",
    "ontology": "http://dati.gov.it/onto/poiapit",
    "semantic_annotation": "POI-AP_IT.PointOfInterestCategory.POIcategoryIdentifier",
    "property_id": "POIcategoryIdentifier",
    "concept_id": "PointOfInterestCategory",
    "ontology_prefix": "poiapit",
    "ontology_id": "POI-AP_IT",
    "concept": "http://dati.gov.it/onto/poiapit#PointOfInterestCategory",
    "property": "http://dati.gov.it/onto/poiapit#POIcategoryIdentifier"
  }
]
```

the idea is to be able to have as much informations as possible to eventually relate the annotation to ontologies and vocabularies.


### example: retrieving a vocabulary dataset

We can obtain a de-normalized, tabular version of the vocabulary `Istat-Classificazione-08-Territorio` using the curl call:

```
curl -X GET http://localhost:9000/kb/v1/hierarchies/properties?vocabulary_name={vocabulary_name}&ontology_name={ontology_prefix}&lang={lang} \ 
-H  "accept: application/json" -H  "content-type: application/json"
```

A `SPARQL` query is used to create a proper tabular representation of the data.

#### example: PontOfInterest / POI_AP-IT

```
curl -X GET http://localhost:9000/kb/v1/hierarchies/properties?vocabulary_name=POICategoryClassification&ontology_name=poiapit&lang=it -H  "accept: application/json" -H  "content-type: application/json"
```

this will return a data structure:

```
[
  {
    "vocabulary": "POI-AP_IT",
    "path": "POI-AP_IT.PointOfInterestCategory.definition",
    "hierarchy_flat": "PointOfInterestCategory",
    "hierarchy": [
      {
        "class": "PointOfInterestCategory",
        "level": 0
      }
    ]
  },
  ...
]
```


#### example: Luoghi Istat / CLV_AP-IT
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

For technical reason, currently a value of `CLV-AP_IT_Region_name` is used in place of `CLV-AP_IT.Region.name`.

### example: retrieve the hierarchies for the properties used 

If we have the example vocabulary `Istat-Classificazione-08-Territorio`, which uses terms from the ontology `clvapit`, we can retrieve the local hierarchy associated to each property with the curl command:

```
$ curl -X GET http://localhost:9000/kb/v1/hierarchies/properties?vocabulary_name={vocabulary_name}&ontology_name={ontology_prefix}&lang={lang} \ 
-H  "accept: application/json" -H  "content-type: application/json"
```

#### example: POI / POI_AP-IT

```
curl -X GET http://localhost:9000/kb/v1/vocabularies/POICategoryClassification?lang=it \ 
-H  "accept: application/json" -H  "content-type: application/json"
```

which will return results:

```
[
  [
    {
      "key": "POI-AP_IT_PointOfInterestCategory_definition",
      "value": "Rientrano in questa categoria tutti i punti di interesse connessi all'intrattenimento come zoo, discoteche, pub, teatri, acquari, stadi, casino, parchi divertimenti, ecc."
    },
    {
      "key": "POI-AP_IT_PointOfInterestCategory_POICategoryName",
      "value": "Settore intrattenimento"
    },
    {
      "key": "POI-AP_IT_PointOfInterestCategory_POICategoryIdentifier",
      "value": "cat_1"
    }
 ],
 ...
]
```


#### example: Luoghi Istat / CLV_AP-IT

```
$ curl -X GET http://localhost:9000/kb/v1/hierarchies/properties?vocabulary_name=Istat-Classificazione-08-Territorio&ontology_name=clvapit&lang=it \ 
-H  "accept: application/json" -H  "content-type: application/json"
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


### example configurations

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

## TODO

+ more documentation / comments
+ more proper tests
+ remove redundant classes for RDFRepository, importing external kb-core dependency, instead


## known ISSUES

...
