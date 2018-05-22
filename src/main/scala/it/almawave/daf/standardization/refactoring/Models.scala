package it.almawave.daf.standardization.refactoring

import java.net.URI

case class ElementWithParent(
  instance_uri: String,
  parent_uri:   Option[String])

case class StdHierarchy(
  uri:  String,
  path: Seq[String] = List()) {

  override def toString() = {
    val _id = uri.replaceAll(".*[#/](.*)", "$1")
    s"""H[${_id}, [${path.mkString(",")}]]"""
  }

}

case class CELL(
  instance_uri:   String,
  property_uri:   String,
  concept_uri:    String,
  ontology_uri:   String,
  vocabulary_uri: String,
  property_value: String,
  property_type:  Option[String],
  property_lang:  Option[String])

case class ROW(
  instance_uri: String,
  hierarchy:    Seq[String],
  cells:        Seq[CELL])

case class VocabularyStadardizedData(
  vocabularyID:  String,
  vocabularyURI: URI,
  ontologyID:    String,
  ontologyURI:   String,
  metadata:      Map[String, CellMetadata],
  data:          Seq[Seq[CellGroup]]       = List())

case class CellMetadata(
  fieldName:     String,
  DAFAnnotation: String,
  propertyType:  Option[String],
  propertylang:  Option[String],
  propertyID:    String,
  propertyURI:   String,
  conceptID:     String,
  conceptURI:    String,
  vocabularyID:  String,
  vocabularyURI: String,
  ontologyID:    String,
  ontologyURI:   String)

case class CellGroup(uri: String, cells: CELL*) extends Seq[CELL] {

  def apply(idx: Int): CELL = this.cells(idx)

  def iterator: Iterator[CELL] = this.cells.iterator

  def length: Int = this.cells.length

}