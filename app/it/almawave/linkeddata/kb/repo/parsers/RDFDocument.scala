package it.almawave.linkeddata.kb.repo.parsers

import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.model.Model

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.eclipse.rdf4j.repository.sail.SailRepository
import scala.collection.mutable.ListBuffer
import org.eclipse.rdf4j.query.QueryLanguage
import org.eclipse.rdf4j.model.util.Models

case class OLDRDFDocument(model: Model) {

  // prefix is used for ontologies
  private var _prefix: Option[String] = None

  def prefix = _prefix

  val BNODES_ON = false

  val vf = SimpleValueFactory.getInstance

  val baseURI = model.getNamespaces
    .map { ns => (ns.getPrefix, ns.getName) }
    .filter(_._1.trim().equals("")) match {
      case empty if (empty.isEmpty) => None
      case items                    => Some(items.head._2)
    }

  val namespaces: Map[String, String] = model.getNamespaces
    .map { ns => (ns.getPrefix, ns.getName) }
    .map { ns =>
      if (ns._1.equals("")) {
        _prefix = Some(ns._2
          .replaceAll("^.*/(.*)[#/]$", "$1")
          .toLowerCase())
        (prefix.get, ns._2)
      } else {
        _prefix = None
        ns
      }
    }
    .toMap

  def contexts = model.contexts().toList

  def subjects = model.subjects().map(_.toString()).toList

  def concepts = sparql
    .query("""SELECT DISTINCT ?concept WHERE { [] a ?concept }""")
    .flatMap { _.map { _._2.toString() } }

  object sparql {

    // TODO: refactorization
    def query(query: String) = {

      val repo = new SailRepository(new MemoryStore)
      repo.initialize()
      val conn = repo.getConnection
      conn.add(model)
      val results = new ListBuffer[Map[String, Object]]
      val tuples = conn.prepareTupleQuery(QueryLanguage.SPARQL, query, baseURI.getOrElse(null)).evaluate()
      while (tuples.hasNext()) {
        results += tuples.next().map { bs => (bs.getName, bs.getValue) }.toMap
      }
      conn.close()
      repo.shutDown()

      results.toStream
    }

  }

  // TODO: find a way to avoid duplicates
  def bnodes = {

    if (BNODES_ON)
      Models.subjectBNodes(model)
        .toStream
    else
      Models.subjectBNodes(model).map { bn =>
        println("ok")
        val id = bn.getID.replaceAll("_:", "")
        s"${baseURI}.well-known/bnode/${id}" // template for well-known bnodes
      }.toStream

  }

  override def toString = {

    s"""
      RDFDocument
      
      prefix: ${prefix}
      baseURI: ${baseURI.getOrElse("")}
      
      namespaces: 
        ${namespaces.mkString("\n\t")}
      
      n° of subjects: ${subjects.distinct.size}
      n° of bnodes: ${bnodes.distinct.size}
      
      concepts:
        ${concepts.mkString("\n\t")}
      
    """.trim()

  }

}
