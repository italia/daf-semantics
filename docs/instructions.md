## example: virtuoso

```bash
mvn install:install-file -DgroupId=virtuoso -DartifactId=virtuoso-jdbc4 -Dversion=0.1 -Dfile=lib/virtjdbc4_2.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=../mvn-repo-awave/repository  -DcreateChecksum=true

mvn install:install-file -DgroupId=virtuoso -DartifactId=virtuoso-rdf4j -Dversion=0.1 -Dfile=lib/virt_rdf4j.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=../mvn-repo-awave/repository  -DcreateChecksum=true
```

----

## example: local lib / project

```bash
mvn install:install-file -DgroupId=it.almawave.linkeddata.kb -DartifactId=kbaselib -Dversion=0.1.6-SNAPSHOT -Dfile=target/kbaselib-0.1.6-SNAPSHOT.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=../mvn-repo-awave/repository -DcreateChecksum=true

mvn clean install -DlocalRepositoryPath=../mvn-repo-awave/repository -DcreateChecksum=true -Dmaven.test.skip=true
```

----

## example

```bash
mvn install:install-file -DgroupId=YOUR_GROUP -DartifactId=YOUR_ARTIFACT -Dversion=YOUR_VERSION -Dfile=YOUR_JAR_FILE -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true
```
