package modules

import java.nio.file.Paths
import java.net.URI
import org.slf4j.LoggerFactory
import java.nio.file.Files

import java.nio.file.Path
import java.net.URL

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

class FileDatastore(val base: String) {

  // TODO: see how to connect this to HDFS or similar

  val logger = LoggerFactory.getLogger(this.getClass)

  val base_path = Paths.get(base).toAbsolutePath().normalize()

  def cache(name: String, doc_path: String) {

    val base_path: Path = try {

      val uri = URI.create(doc_path).normalize()
      val url = uri.toURL()

      val path = Paths.get(base, name, uri.getPath.substring(uri.getPath.lastIndexOf("/")))

      val dir = path.toFile().getParentFile
      if (!dir.exists()) dir.mkdirs()

      if (!path.toFile().exists()) {
        Files.copy(url.openStream(), path)
      } else {
        println(s"KB> ${name} already cached in ${path}!")
      }

      path

    } catch {
      case ex =>
        ex.printStackTrace()

        var base_path = base

        if (base.startsWith("file:/"))
          base_path = base.replaceFirst("^.*(/.*)$", "$1")

        Paths.get(base_path).toAbsolutePath().normalize()

    }
  }

  def list(ext: String*): Stream[URI] = {

    Files.walk(base_path).iterator().toStream
      .filter(_.toFile().isFile())
      .filter(_.toString().matches(s".*\\.(${ext.mkString("|")})"))
      .map(_.toUri().normalize())

  }

}

object MainFileDatastore extends App {

  val fs = new FileDatastore("ontologies")

  fs.cache("foaf", "http://xmlns.com/foaf/spec/index.rdf")

  val files = fs.list("owl", "rdf")
  files.foreach { x => println(x) }

}

//class FileDatastoreTest {
//
//  @Test
//  def test_local_relative_files {
//    var fs = new FileDatastore("ontologies")
//    Assert.assertTrue(fs.list("owl", "ttl").size > 0)
//  }
//
//  @Test
//  def test_remote_files {
//    var fs = new FileDatastore("file:///C:/Users/Al.Serafini/repos/DAF/lod_manager/ontologies/mibact/cultural-ON.owl")
//    Assert.assertTrue(fs.list("rdf").size > 0)
//  }
//
//}

