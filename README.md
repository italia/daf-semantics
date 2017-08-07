
lod_manager
===============

SEE: [teamdigitale/daf](https://github.com/teamdigitale/daf) 

## instructions

1. local publish of dependencies

	1.1. `kb-core`

	find a better way to manage the engine part as an external library: for example it could be helpful to have a specific git repository, using a standard sbt dependency in order to import the jar.
	This way the engine could be more easily re-used for other services such as validators, etc.
	At the moment the "engine" code base have been merged into the main repository, for simplicity.

	1.2. virtuoso JDBC / RDF4J jar

	The dependencies for virtuoso integration are currently not yet published on the maven central, so they are linked using the convetional `lib` folder in the sbt project:

	```
	[lod_manager]
	├───/lib
	│   ├───virtjdbc4_2.jar
	│   └───virt_rdf4j.jar
	```

	We could avoid this if/when the libraries will be published, or by publishing them on a private nexus.

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

### adapters for RDF4J collections

The object `it.almawave.kb.utils.RDF4JAdapters` contains some adapters which may be useful for working with RDF4J collections in a simpler way.

+ `StringContextAdapter` can be used for writing conversions like:
`val context:Resource = "http://graph".toIRI`
+ `StringContextListAdapter` it's the same on collections, and can be used for writing conversions like:
`val contexts:Array[Resource] = Array("http://graph").toIRIList`
+ `RepositoryResultIterator` and `TupleResultIterator` are useful for handling results from query as Scala collections, without having to write `while(...)` code.
+ `BindingSetMapAdapter` adds a `toMap` conversion method, useful for writings things like:
```
val bs: BindingSet = ...
val map: Map[String, Object] = bs.toMap()
```

### Try + transaction handling + error messages

It's possible to simplify the code used for interacting with the underling RDF4J Repository instance, focusing on the actual code, using the `RepositoryAction` construct like in the following example:

```
import it.almawave.kb.utils.TryHandlers._

...

implicit val logger = LoggerFactory.getLogger(this.getClass)

val repo:Repository = ...

// a method clearing all the triples!
def clear_all() {

	RepositoryAction(repo) { conn =>

	// some code using connection object, like for example:
	conn.clear()

	}(s"cannot clear all the triples for some reason!}")

}

```

**NOTE**:
+ the open/close connection actions are handled by the `RepositoryAction` itself 
+ the provided error message will be used internally both for logging on the chosen logger, and for handling Exception in the usual `Try/Failure` way.


* * *

## TODO

- [ ] publish `kb-core` on github / bitbucket or as sub-module
- [ ] add `kb-core` dependency on sbt
- [x] add `RDF4J` dependencies on sbt
- [ ] add `virtoso` dependencies on sbt
- [x] add `virtoso` jar on sbt/lib
- [ ] refactoring JUnit tests for engine part: memory
- [ ] refactoring JUnit tests for engine part: virtuoso wrapper
- [ ] more test coverage for simple example HTTP requests (specs2)
- [x] ~~datapackage or similar? at the moment~~ a `.metadata` file is used for contexts
- [x] ~~creating a simple construct for dealing with transactions~~. Done: see `TryHandlers.RepositoryAction[X]`




