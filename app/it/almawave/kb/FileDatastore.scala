package it.almawave.kb

import java.nio.file.Paths
import java.net.URI
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config

/*
 * draft for an helper class for handling files.
 * The idea is to encapsulate the logic for file access: read, save etc,
 * enabling later usage of HDFS and so on...
 */
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
      case ex: Exception =>
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

  // this method attemps to find a .metadata file related to the vocabulary
  def getMetadata(uri: URI): Config = {

    val file = Paths.get(uri)
    val dir = file.getParent
    val file_name = file.getFileName.toString().replaceAll("(.*)\\..*", "$1")
    val file_metadata = Paths.get(dir.toString(), s"${file_name}.metadata")

//    println("IMPORT.CHECK.....")
//    println("SOURCE.FILE: " + file)
//    println("SOURCE.URI: " + uri)
//    println("file_name: " + file_name)
//    println("file_metadata: " + file_metadata)
//    println()
    
    var conf = ConfigFactory.empty()
    if (Files.exists(file_metadata)) {

//      println("META OK! " + file_metadata)
      conf = conf.withFallback(ConfigFactory.parseURL(file_metadata.toUri().toURL()))

      println(conf)
    }

    conf
  }

}

object MainFileDatastore extends App {

  val fs = new FileDatastore("ontologies")

  fs.cache("foaf", "http://xmlns.com/foaf/spec/index.rdf")

  val files = fs.list("owl", "rdf")
  files.foreach { uri =>
    println("\n" + uri)
  }

}


