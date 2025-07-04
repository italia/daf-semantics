
semantic_standardization
==========================

This project is currently a POC exploring the standardization of terms used in a dataset, given a controlled vocabulary.

For each vocabulary inside the [OntoPiA](https://github.com/italia/dati-semantic-assets/) network, a conventional tabular in-memory representation is produced. Those intermediate data are used as the base for  creating a CSV-like representation, as well as a set of metadata (see [metadata.semantics](https://daf-docs.readthedocs.io/en/latest/datamgmt/metacatalog.html#metadata.semantics)), useful to enrich the cells of the internal daf-datamodel. 


## endpoints descriptions

### vocabulary datasets list

We can obtain a list of the vocabularies, currently loaded as datasets, using the endpoint: `/daf/standardization/vocabularies`

#### example: getting the list of vocabulary datasets

For example the following curl command:

```bash
curl -X GET http://localhost:7777/kb/api/v1/daf/standardization/vocabularies?lang=it \ 
	-H  "accept: application/json" \ 
	-H  "content-type: application/json"
```
will return the URI and the title for each vocabulary, which can be used later as a parameter for the other endpoints.

### metadata for a vocabulary dataset

We can obtain the metadata for each field of the vocabulary dataset, using the endpoint: `/daf/standardization/{vocabularyID}/metadata`

#### example: getting the fields metadata for a vocabulary dataset

For example the following curl command:
```bash
curl -X GET http://localhost:7777/kb/api/v1/daf/standardization/licences/metadata?lang=it \
	-H  "accept: application/json" 
```
will return a series of metadata to be associated to a specific field (cell) in the dataset, including the semantic annotation, the related vocabulary URI and ontologyURI, and so on.

### data for a vocabulary dataset (CSV)

We can obtain the data of a vocabulary dataset, in CSV format, using the endpoint: `  
/daf/standardization/{vocabularyID}.csv`

#### example: getting the CSV data for a vocabulary

For example the following curl command:
```bash
curl -X GET http://localhost:7777/kb/api/v1/daf/standardization/licences.csv?lang=it \ 
	-H  "accept: text/csv" 
```
will return a CSV containing all the selected data for a given vocabulary, de-normalized in a tabular form.


## TODO

+ expand the description of the process
+ add a minimal proper test coverage


## known issues

+ the geographical classification case whould be reviewed, as at the moment is the only case where the vocabulary will be not written using (only) SKOS.



* * * 
SEE ALSO: [gitlab repository @ almawave](http://10.121.172.7:10080/public-od/daf/semantic-standardization)


