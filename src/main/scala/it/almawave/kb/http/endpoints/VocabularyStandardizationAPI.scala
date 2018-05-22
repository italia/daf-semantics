package it.almawave.kb.http.endpoints

import java.net.URL
import java.nio.file.Paths

import javax.inject.Inject
import javax.ws.rs._
import io.swagger.annotations.{ Api, ApiOperation, ApiParam, Tag }
import javax.ws.rs.core.{ MediaType, Response }
import com.sun.net.httpserver.Authenticator.Success
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.io.Source
import it.almawave.daf.standardization.v1.StandardizationProcessV1
import javax.ws.rs.core.StreamingOutput
import java.io.OutputStream
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.ByteArrayOutputStream

/**
 * This classes exposes various endpoints for extracting the dataset, extracted from a given vocabulary,
 * as well as its metadata.
 *
 * REVIEW: usage of lang
 */
@Api
@Path("/daf/standardization")
class VocabularyStandardizationAPI {

  val logger = LoggerFactory.getLogger(this.getClass)

  @Inject
  var service: CatalogStandardizationService = null

  // TODO: lookup: given a field name, retrieves vocID, ontoID

  @GET
  @Produces(Array(MediaType.APPLICATION_JSON))
  @ApiOperation(nickname = "vocabulariesStandardizationList", value = "list of vocabularies, available as datasets, for standardization")
  @Path("/vocabularies")
  def vocabularies_list(@QueryParam("lang")@DefaultValue("it") lang: String = "it") = {

    val cstd = service.stardardizer

    cstd.getVocabularyStandardizersList()
      .map { item =>
        val meta = item.vbox.meta

        val id = meta.id
        val url = meta.url
        val title = meta.titles.filter(_.lang.equals(lang)).map(_.value).headOption.getOrElse("")

        Map(
          "vocabularyID" -> id,
          "title" -> title,
          "url" -> url)

      }.distinct

  }

  @GET
  @Produces(Array(MediaType.APPLICATION_JSON))
  @ApiOperation(nickname = "vocabularyStandardizationJSON", value = "standardization of a vocabulary, JSON")
  @Path("/{vocabularyID}.json")
  def details_json(
    @PathParam("vocabularyID")@DefaultValue("licences") vocabularyID: String,
    @QueryParam("lang")@DefaultValue("it") lang:                      String = "it") = {

    val cstd = service.stardardizer
    val vstd = cstd.getVocabularyStandardizerByID(vocabularyID).get

    vstd.toJSONTree()

  }

  @GET
  @Produces(Array(MediaType.APPLICATION_JSON))
  @ApiOperation(nickname = "vocabularyStandardizationMetadata", value = "standardization of a vocabulary, metadata")
  @Path("/{vocabularyID}/metadata")
  def metadata(
    @PathParam("vocabularyID")@DefaultValue("licences") vocabularyID: String,
    @QueryParam("lang")@DefaultValue("it") lang:                      String = "it") = {

    val vstd = service.stardardizer
      .getVocabularyStandardizerByID(vocabularyID).get

    vstd.getMetadata()

  }

  // TODO: LOOKUP by field name -> ontoID, vocID

  @GET
  @Produces(Array("text/csv", "application/csv", "text/plain"))
  @ApiOperation(nickname = "vocabularyStandardizationCSV", value = "standardization of a vocabulary, CSV")
  @Path("/{vocabularyID}.csv")
  def details_csv(
    @PathParam("vocabularyID")@DefaultValue("licences") vocabularyID: String,
    @QueryParam("lang")@DefaultValue("it") lang:                      String = "it") = {

    val cstd = service.stardardizer
    val vstd = cstd.getVocabularyStandardizerByID(vocabularyID).get

    val baos = new ByteArrayOutputStream
    vstd.toCSV()(baos)
    val content = baos.toString()
    baos.flush()
    baos.close()
    content

  }

}