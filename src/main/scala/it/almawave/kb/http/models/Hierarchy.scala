package it.almawave.kb.http.models

import scala.collection.mutable.ListBuffer

case class Hierarchy (
  codice: String,
  label: String,
  uri: String,
  parent_uri: String,
  children: ListBuffer[Hierarchy]//offspring
)