package it.almawave.kb.http.models

import it.almawave.linkeddata.kb.catalog.models.URIWithLabel
import it.almawave.linkeddata.kb.catalog.models.ItemByLanguage
import it.almawave.linkeddata.kb.catalog.models.Version
import it.almawave.linkeddata.kb.catalog.models.LANG

// REVIEW
case class OntologyMetaModel(

  id: String,
  source: String,
  url: String,
  prefix: String,
  namespace: String,
  concepts: Set[String],
  imports: Set[URIWithLabel],
  titles: Seq[ItemByLanguage],
  descriptions: Seq[ItemByLanguage],
  versions: Seq[Version],
  creators: Set[URIWithLabel],

  // CHECK with provenance
  publishedBy: String,
  owner: String,
  langs: Seq[LANG],
  lastEditDate: String,
  license: URIWithLabel,

  tags: Seq[URIWithLabel],
  categories: Seq[URIWithLabel],
  keywords: Seq[String],
  // CHECK with provenance

  provenance: Seq[Map[String, Any]])