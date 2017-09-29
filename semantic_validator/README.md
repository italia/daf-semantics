semantic_validator
==================

The Semantic Validator is a component (based on RDF4J) designed to provide a simple way for validating RDF metadata dataset against a specific Ontology on an underlying triplestore.

The triplestore used is in-memory, rules actually are sparql queries executed in distinct repositories for the sigle validation request, in order to isolate the validations. 

![semantic_repository component inside the semantic_manager architecture](./docs/semantic_validator-v0.1.0.png)


## HTTP API

+ entrypoint for the play application (swagger-ui)
[http://localhost:9000](http://localhost:9000)

+ swagger definition
[http://localhost:9000/spec/semantic_validator.yaml](http://localhost:9000/spec/semantic_validator.yaml)


## instructions

1. compile / package

	```bash
	$ sbt clean package
	```

2. run

	```bash
	$ sbt run 
	```
### Validation rules

The validator is configured with three vocabularies:
	
- *DCAT-AP_IT* : rules set derived from the original [SEMICeu/dcat-ap_validator](https://github.com/SEMICeu/dcat-ap_validator) and modified in order to be compliant with the AP_IT profile constraints
- *DCAT-AP* : the original rules set [SEMICeu/dcat-ap_validator](https://github.com/SEMICeu/dcat-ap_validator) 
- *CPSV-AP* : the original rules set [catalogue-of-services-isa/cpsv-ap_validator](https://github.com/catalogue-of-services-isa/cpsv-ap_validator) 

The directory stucture is modular in order to add new validators, rules and methods of validation.
 
The validators have to be configured in the *validator.conf* file under the *conf* directory
```
Semantic_Validator
│   README.md
│   build.sbt
|   ...    
│
└───conf
│   application.conf
│   validator.conf
│   ...
└───dist
	└─────data
		  └─────ontologies
                └─────agid
			    	  └─────DCAT-AP_IT
							│	DCAT-AP_IT.owl
							│	Licenze.ttl
							│	vcard-ns.ttl
                            │   ...
	                        └────validators
                                 └────sparql
                                      rule-0.rq
                                      rule-1.rq
                                      rule-2.rq
                                      ...
```
* * *

OWL and ttl files in the specific ontology directory (ex: dist/data/ontologies/agid/DCAT-AP_IT) and the dataset sended to the service, are loaded together into the repository (in-memory) in order to use RDFS inference during the validation.

## TODO

- [ ] add an implementation for shacl validations
- [ ] ad a CPSV-AP_IT validator
- [ ] ...

* * *

SEE: [teamdigitale/daf](https://github.com/teamdigitale/daf) 
