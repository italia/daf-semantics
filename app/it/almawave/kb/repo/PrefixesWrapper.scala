package it.almawave.kb.repo

import scala.util.Try
import org.eclipse.rdf4j.model.vocabulary._
import org.eclipse.rdf4j.repository.Repository
import org.slf4j.LoggerFactory
import it.almawave.kb.utils.TryHandlers._
import it.almawave.kb.utils.RDF4JAdapters._

class PrefixesHelper(repo: Repository) {

  implicit val logger = LoggerFactory.getLogger(this.getClass)

  def clear() = {

    val conn = repo.getConnection
    conn.begin()

    TryLog {

      conn.clearNamespaces()
      conn.commit()

    }(s"KB:RDF> error while removing namespaces!")

    conn.close()

  }

  def add(namespaces: (String, String)*) {

    val conn = repo.getConnection
    conn.begin()

    TryLog {

      namespaces.foreach { pair => conn.setNamespace(pair._1, pair._2) }
      conn.commit()

    }(s"KB:RDF> cannot add namespaces: ${namespaces}")

    conn.close()

  }

  def remove(namespaces: (String, String)*) {

    val conn = repo.getConnection
    conn.begin()

    TryLog {

      namespaces.foreach { pair => conn.setNamespace(pair._1, pair._2) }
      conn.commit()

    }(s"KB:RDF> cannot remove namespaces: ${namespaces}")

    conn.close()

  }

  // get prefixes
  def list(): Try[Map[String, String]] = {

    val conn = repo.getConnection

    val results = TryLog {

      conn.getNamespaces.toList
        .map { ns => (ns.getPrefix, ns.getName) }
        .toMap

    }("cannot retrieve a list of prefixes")

    conn.close

    results

  }

  val DEFAULT = Map(
    OWL.PREFIX -> OWL.NAMESPACE,
    RDF.PREFIX -> RDF.NAMESPACE,
    RDFS.PREFIX -> RDFS.NAMESPACE,
    DC.PREFIX -> DC.NAMESPACE,
    FOAF.PREFIX -> FOAF.NAMESPACE,
    SKOS.PREFIX -> SKOS.NAMESPACE,
    XMLSchema.PREFIX -> XMLSchema.NAMESPACE,
    FN.PREFIX -> FN.NAMESPACE,
    "doap" -> DOAP.NAME.toString(), // SEE: pull request
    "geo" -> GEO.NAMESPACE,
    SD.PREFIX -> SD.NAMESPACE)

}