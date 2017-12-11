//package it.almawave.linkeddata.kb.repo.parsers
//
//import java.io.File
//import org.eclipse.rdf4j.rio.Rio
//import org.eclipse.rdf4j.rio.RDFFormat
//import java.io.StringReader
//import java.io.FileReader
//import scala.util.Try
//
//object RDFBasicMetadataExtractor {
//
//  def info(rdf_file: File) = {
//    List(RDFFormat.TURTLE, RDFFormat.NTRIPLES, RDFFormat.JSONLD, RDFFormat.RDFXML)
//      .map { format =>
//        Try {
//          val model = Rio.parse(new FileReader(rdf_file), "", format)
//          val res = ((rdf_file, format.getDefaultMIMEType), model.toArray().size)
//          res
//        }
//      }
//      .filter(_.isSuccess)
//  }
//
////  case class RDF(name: String, format: RDFFormat, triples: Int)
//
//}
//
//object MainRecognizeFormat extends App {
//
//  RDFBasicMetadataExtractor.info(new File("dist/data/ontologies/foaf/foaf.rdf"))
//    .foreach { item => println(item) }
//
//  //  var ext = new RDFBasicMetadataExtractor()
//  //  ext = new RDFBasicMetadataExtractor(new File("dist/data/ontologies/agid/CPSV-AP_IT/CPSV-AP_IT.owl"))
//
//}