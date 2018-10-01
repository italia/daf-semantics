
daf-semantics
====================

The Daf Semantics repository collects some different components, designed for integrating ontologies, RDF data and to provide some different "semantic" functionalities to the [DAF](https://github.com/italia/daf) platform.

The architecture is internally splitted into some different microservices:
<img src="./docs/semantic_manager-v4.png" alt="semantic_manager" width="60%" height="auto">

We can thus recognize 4 different areas:

+ catalog frontend
	- [DAF dataportal](https://github.com/italia/daf-dataportal) is the DAF front-end, where all the datasets are available. It includes a section with information about ontologies/vocabularies, and a page for validating metadata, using a standard validator.
	- [katalod](http://10.121.172.7:10080/public-od/daf/katalod.git) [WIP] is the backend for the "catalog" frontend: it exposes some different endpoints to DAF dataportal, designed ad hoc to provide a conventional data and metadata access from the ontologies and controlled vocabularies from the [OntoPiA network](https://github.com/italia/daf-ontologie-vocabolari-controllati).
	- [semantic_frontend](https://github.com/italia/daf-semantics/tree/master/semantic_frontend) mockup was designed as a first guideline for developing an ontologies/vocabularies catalog front-end
	- [OntoPiA-UI](https://github.com/anuzzolese/OntoPiA-UI)
a docker image, containing a collection of tools useful for exploring the OntoPiA network of ontologies.


+ standardized data ingestion into DAF:
	+ [ontonethub](https://github.com/teamdigitale/ontonethub)
	a component providing indexing/search capabilities for the catalog. The microservice offers a convetional way for annotating incoming datasets.
	+ [semantic_standardization](https://github.com/italia/daf-semantics/tree/master/semantic_standardization) [POC]
	a component designed to support a simple normalization of values inside datasets, using the controlled values defined inside the vocabularies of the OntoPiA network.


+ RDF / linked data and triplestore
	+ [kbaselib](http://10.121.172.7:10080/public-od/daf/kbaselib.git) [WIP]
	a library collecting several different functionalities, shared between other components. It can be used by its own, providing a simple abstraction over a triplestore, using the standard RDF4J interface.
	+ [semantic mapping / triplifier] [WIP]
	a component for mapping of incoming data (typically from JDBC, or CSV) to RDF, using W3C standards
	+ The [semantic_manager](https://github.com/italia/daf-semantics/tree/master/semantic_manager) was designed as the central access point for a subset of emerging use case, and for loading data in both `semantic_repository` and `ontonethub` components. The microservice acts more or less as an internal API gateway, and its revision is currently planned.
	+ [semantic_repository](https://github.com/seralf/daf-semantics/tree/master/semantic_repository)
an abstraction over different triplestores (for example: in-memory, virtuoso).

+ metadata validation and production
	+ [semantic_validator](https://github.com/italia/daf-semantic-validator) [WIP]
	a component for validating an ontology over DCAT-AP_IT standard
	+ [semantic_spreadsheet](https://github.com/italia/daf-semantics/tree/master/semantic_spreadsheet)
	a repository collecting recipes for creating RDF data from spreadsheets, using google refine


[OntoPiA](https://github.com/italia/daf-ontologie-vocabolari-controllati)
The ontologies / controlled-vocabularies network
