
lod_manager
===============

SEE: [teamdigitale/daf](https://github.com/teamdigitale/daf) 

## instructions

1. local publish of dependencies (`kb-core`)

~~At the moment the wrapper for the triplestore(s) is published as a jar dependency, using the `lib` folder:~~

```
[lod_manager]
├───/lib
│   ├───eclipse-rdf4j-2.2.1-onejar.jar
│   └───kb-core-0.0.1.jar
```

~~Copy the library under `/lib` before compilation (NOTE: the libraries should be managed too by sbt).~~

**NOTE** : for simplicity the library sources have been merged into the main repository.

**TODO** : find a better way to manage the engine part as an external library: for example it could be helpful to have a specific git repository, and an sbt dependency importing the jar. This way the engine could be more easily re-used for other services such as validators, etc.

2. compile / package

```
$ sbt clean package
```

3. run

```
$ sbt run 
```

4. (local) deploy

```
$ sbt clean dist
$ unzip -o -d  target/universal/ target/universal/lod-manager-0.0.1.zip
$ target/universal/lod-manager-0.0.1/bin/lod-manager  -Dconfig.file=./conf/application.conf
```

**NOTE**: if the application crashed, the pid file whould be deleted before attempting re-run 
```
$ rm target/universal/lod-manager-0.0.1/RUNNING_PID 
```

5. release

working draft: [0.0.1](https://github.com/seralf/lod_manager/releases/tag/0.0.1)


* * * 

## TODO 

- [ ] publish `kb-core` on github / bitbucket or as sub-module
- [ ] add `kb-core` dependency on sbt 
- [x] add `RDF4J` dependencies on sbt (if needed)
- [ ] more test coverage for simple example HTTP requests
- [x] ~~datapackage or similar? at the moment~~ a `.metadata` file is used for contexts


