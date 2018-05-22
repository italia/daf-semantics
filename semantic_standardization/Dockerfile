
# JAVA
FROM openjdk:8-jre-alpine

# application
WORKDIR katalod

# general configurations
# LABEL it.almawave.daf.katalod.version="0.0.5"
# LABEL vendor="Almawave"
# LABEL it.almawave.daf.katalod.date="2018-04-11"
# ENV kb_version 0.0.5

ADD conf/ conf/
ADD ontologie-vocabolari-controllati/ ontologie-vocabolari-controllati/
ADD src/main/swagger-ui src/main/swagger-ui
ADD target/libs /usr/share/katalod/lib
ADD target/kataLOD-0.0.5.jar /usr/share/katalod/kataLOD-0.0.5.jar

ENTRYPOINT ["/usr/bin/java", "-cp", "/usr/share/katalod/lib/*:/usr/share/katalod/kataLOD-0.0.5.jar", "it.almawave.kb.http.MainHTTP"]

EXPOSE 7777