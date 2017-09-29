package it.almawave.linkeddata.kb.repo

import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.eclipse.rdf4j.repository.Repository
import java.io.File
import scala.util.Try
import org.eclipse.rdf4j.rio.Rio
import java.io.FileInputStream
import java.net.URLDecoder
import org.eclipse.rdf4j.query.QueryLanguage

object RDFRepository {

  def memory() = {

    new RDFRepository(new SailRepository(new MemoryStore))

  }

}

class RDFRepository(repo: Repository) {

  def start() = Try {
    if (!repo.isInitialized())
      repo.initialize()
  }

  def stop() = Try {
    if (repo.isInitialized())
      repo.shutDown()
  }

  object store {

    def clear(contexts: String*) = Try {

      val conn = repo.getConnection

      val vf = conn.getValueFactory
      val ctxs = contexts.map { cx => vf.createIRI(cx) }

      conn.clear(ctxs: _*)

      conn.close()

    }

  }

  object io {

    def addRDFFile(rdfFile: File, mimeFormat: String, contexts: String*) = Try {

      val conn = repo.getConnection
      val vf = conn.getValueFactory
      val ctxs = contexts.map { cx => vf.createIRI(cx) }

      val format = Rio.getParserFormatForMIMEType(mimeFormat).get
      val fis = new FileInputStream(rdfFile.getAbsoluteFile)

      contexts.foreach { context =>

        val _context = URLDecoder.decode(context, "UTF-8")

        val doc = Rio.parse(fis, context, format, vf.createIRI(context))

        conn.add(doc, ctxs: _*)

      }

      fis.close()

      conn.close()

    }

  }

  object sparql {

    import it.almawave.linkeddata.kb.repo.utils.RDF4JAdapters._

    def query(query: String) = Try {

      val conn = repo.getConnection

      val results = conn.prepareTupleQuery(QueryLanguage.SPARQL, query)
        .evaluate()
        .toStream
        .map(_.toMap())
        .toList

      conn.close()

      results
    }

  }

}