
daf-semantics
====================

The Daf Semantics repository collects some different components, designed for integrating ontologies, RDF data and to provide some different "semantic" functionalities to the [DAF](https://github.com/italia/daf) platform.

The [semantic_manager]() component exposes the central access point for a subset of the microservices' functionalities:
<img src="./docs/semantic_manager-v4.png" alt="semantic_manager" width="60%" height="auto">

The planned components are:

+ ***semantic_frontend***: 
the front end for the OntoPA catalog [TODO]
+ ***semantic_manager***: 
the main interface between DAF and the daf-semantics microservices [WIP]
+ ***ontonethub***: 
a component providing indexing/search capabilities for the catalog [WIP]
+ ***semantic_repository***: 
an abstraction over different triplestores [WIP]
+ ***semantic_validator***: 
a component for validating an ontology over DCAT-AP_IT standard [WIP]
+ ***semantic_standardization***: 
a component exposing vocabulary data and hierarchies, useful for simple standardization [POC]
+ ***semantic_spreadsheet***: 
a repository collecting recipes for creating RDF data from spreadsheets, using google refine [WIP]
+ ***semantic_mapping***: 
a component for mapping of incoming data (typically in CSV) to RDF, using W3C standards [TODO]

