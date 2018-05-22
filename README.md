semantic_validator
==================

The Semantic Validator is a component (based on RDF4J) designed to provide a simple way for validating RDF metadata dataset against a specific Ontology on an underlying triplestore.

The triplestore used is in-memory, rules actually are sparql queries executed in distinct repositories for the sigle validation request, in order to isolate the validations.

![semantic_repository component inside the semantic_manager architecture](./docs/semantic_validator-v4.png)


## HTTP API

+ entrypoint for the play application (swagger-ui)
[http://localhost:9000](http://localhost:9000)

+ swagger definition
[http://localhost:9000/spec/semantic_validator.yaml](http://localhost:9000/spec/semantic_validator.yaml)

The validator is currently based on a set of queries (about 150 for DCAT-AP_IT) returning a record of information for the rules broken by the dataset, the most important infos are:

- Class name: the class involved in the rule (ex: Organization for DCAT-AP_IT)
- Rule ID: the broken rule id (ex: 207 for DCAT-AP_IT) 
- Error description: the problem description (ex: "vcard:hasURL should be a resource" for DCAT-AP_IT)

there are two endpoints:

- `/validate`   : in order to validate a document
- `/validators`: in oder to ghe the list of available validators 


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

The validator is now configured with three vocabularies:

- *DCAT-AP_IT* : rules set derived from the original [SEMICeu/dcat-ap_validator](https://github.com/SEMICeu/dcat-ap_validator) and modified in order to be compliant with the AP_IT profile constraints
- *DCAT-AP* : the original rules set [SEMICeu/dcat-ap_validator](https://github.com/SEMICeu/dcat-ap_validator) 
- *CPSV-AP* : the original rules set [catalogue-of-services-isa/cpsv-ap_validator](https://github.com/catalogue-of-services-isa/cpsv-ap_validator) 

The directory stucture is modular in order to add new validators, rules and methods of validation.

The validators have to be configured in the *validator.conf* file under the *conf* directory

```
Semantic_Validator
│ README.md
│ build.sbt
│ ...
│
└-conf
│ application.conf
│ validator.conf
│ ...
└-dist
  └-──data
    └-──ontologies
      └-──agid
        └-──DCAT-AP_IT
          │ DCAT-AP_IT.owl
          │ Licenze.ttl
          │ vcard-ns.ttl
          │ ...
          └-─validators
           └-─sparql
              rule-0.rq
              rule-1.rq
              rule-2.rq
              ...
```
* * *

OWL and ttl files in the specific ontology directory (ex: dist/data/ontologies/agid/DCAT-AP_IT) and the dataset sended to the service, are loaded together into the repository (in-memory) in order to use RDFS inference during the validation.


* * *

## running / testing the microservice

The most simple way to test the application locally, is running it directly from sbt

```bash
$ sbt clean compile
$ sbt run
```

Another option is to prepare the distribution to deploy with sbt, then run it.
For example we could:

```bash
$ sbt clean dist
$ unzip -o -d  target/universal/ target/universal/semantic_validator-1.0.1.zip
$ cd target/universal/semantic_validator-1.0.1
$ bin/semantic_validator -Dconfig.file=./conf/production.conf
```

Preparing a docker image is strightforward as:

```bash
$ sbt docker:stage
$ sudo docker build target/docker/stage/
$ sudo docker run -p 9000:9000 {image_id}
```

And finally we can build and publish locally the docker image to run, by:

```bash
$ sbt clean compile
$ sudo sbt docker:publishLocal
$ sudo docker run -p 9000:9000 {image_id}
```

* * *



## TODO

- [ ] add an implementation for shacl validations
- [ ] add a CPSV-AP_IT rules set for the validator
- [ ] ...

* * *

**SEE ALSO**: [teamdigitale/daf](https://github.com/teamdigitale/daf) 

[README last updated: 2018-05-22]



