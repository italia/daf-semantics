package modules

import java.net.URI

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.setAsJavaSet
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.io.Source

import com.google.inject.ImplementedBy

import javax.inject.Inject
import javax.inject.Singleton
import play.api.Application
import play.api.Configuration
import play.api.Environment
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import repositories.FileDatastore
import repositories.RDFRepository
import scala.collection.mutable.ArrayBuffer
import semantic_validator.yaml.Validation
import semantic_validator.yaml.ValidationDetail
import semantic_validator.yaml.Validator
import java.io.File
import semantic_validator.yaml.ValidationResult
import semantic_validator.yaml.ValidationDetailRuleSeverity
import org.eclipse.rdf4j.repository.RepositoryConnection
import java.io.InputStream
import org.eclipse.rdf4j.model.ValueFactory
import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import java.time.Instant
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap
import modules.utilities.ConfigHelper

@ImplementedBy(classOf[ValidatorModuleBase])
trait ValidatorModule

@Singleton
class ValidatorModuleBase @Inject() (lifecycle: ApplicationLifecycle) extends ValidatorModule {

  val logger = Logger.underlyingLogger
  var okValidators = ArrayBuffer[Configuration]()
  var dir_ontologies: String = ""
  var file_ontologies = ArrayBuffer[File]()

  /**
   * metodo che ritorna la lista dei validatori disponibili
   */
  def getValidators() = {

    var res = okValidators.map(f => {
      Validator(
        f.getString("name").get,
        f.getString("description").get,
        f.getString("ontology").get,
        f.getString("id").get,
        f.getString("publisher").get,
        f.getString("type").get)
    }).toList

    res

  }

  /**
   * metodo che ritorno il risultato di una validazione
   */
  def doValidation(name: String, rdfDocument: File, validator: String, rdfsinf: Boolean): Validation = {

    var res: Validation = null;

    /**
     * TODO devo mantenere un pool di repository altrimenti su richieste contemporanee i repository vengono sovrascritti...?!?!?
     */

    //creo il repository in memoria
    val kbrepo = RDFRepository.memory(rdfsinf)
    //inizializza il repository RDF
    kbrepo.start();

    var det = Seq[ValidationDetail]()
    val unixTimestamp: String = Instant.now.toString()

    //verifico se il validatore richiesto è tra quelli validi
    if (okValidators.exists(v => v.getString("id").get.equalsIgnoreCase(validator))) {

      //recupero il file RDF e lo carico il file nel repository
      //val context = "http://validator/context/" + name + "/" + unixTimestamp
      //val ctx = SimpleValueFactory.getInstance.createIRI(context.trim())
      logger.debug(s"loading doc to validate in ${kbrepo}")
      var loaded = kbrepo.loadRDF(name, rdfDocument, null)
      logger.debug(s"triples found in ${kbrepo} : ${kbrepo.countTriples(null)}")

      if (loaded.equals("OK")) {

        var n_blanks_meta = 0
        logger.debug("-----------------------")
        var l: ListBuffer[HashMap[String, String]] = kbrepo.execQuery("PREFIX dcatapit: <http://dati.gov.it/onto/dcatapit#> SELECT (count(?s) AS ?n_blanks_meta) WHERE { ?s ?p ?o . FILTER(isBlank(?s)) }")
        l.foreach((hm) => { n_blanks_meta = Integer.parseInt(hm.get("n_blanks_meta").get) })
        logger.debug("n_blanks_meta : [" + n_blanks_meta + "]")

        /**
         * TODO:inserisce un errore in presenza di blank nodes
         * if(n_blanks_meta>0) {
         *
         * val vd : ValidationDetail = new ValidationDetail("@blank","@blank", "@blank nodes not allowed in metadata", "@blank","@blank node not allowed","@blank",-1, new ValidationDetailRuleSeverity("error"))
         * det = det ++ Seq(vd)
         *
         * }
         *
         */

        //carico le ontologie
        file_ontologies.foreach((fo) => {
          if (loaded.equals("OK")) {
            logger.debug(s"loading onto [${fo.getName}] in ${kbrepo}")
            loaded = kbrepo.loadRDF(fo.getName, fo, null)
            logger.debug(s"triples found in ${kbrepo} : ${kbrepo.countTriples(null)}")
          }
        })

        if (loaded.equals("OK")) {

          l = kbrepo.execQuery("PREFIX dcatapit: <http://dati.gov.it/onto/dcatapit#> SELECT (count(?s) AS ?n_blanks) WHERE { ?s ?p ?o . FILTER(isBlank(?s)) }")
          l.foreach((hm) => { logger.debug(hm.toString()) })
          l = kbrepo.execQuery("PREFIX dcatapit: <http://dati.gov.it/onto/dcatapit#> SELECT (count(?s) AS ?n_Agent) WHERE { ?s a dcatapit:Agent }")
          l.foreach((hm) => { logger.debug(hm.toString()) })
          l = kbrepo.execQuery("PREFIX dcatapit: <http://dati.gov.it/onto/dcatapit#> SELECT (count(?s) AS ?n_Dataset) WHERE { ?s a dcatapit:Dataset }")
          l.foreach((hm) => { logger.debug(hm.toString()) })
          l = kbrepo.execQuery("PREFIX dcatapit: <http://dati.gov.it/onto/dcatapit#> SELECT (count(?s) AS ?n_Organization) WHERE { ?s a dcatapit:Organization }")
          l.foreach((hm) => { logger.debug(hm.toString()) })
          l = kbrepo.execQuery("PREFIX dcatapit: <http://dati.gov.it/onto/dcatapit#> SELECT (count(?s) AS ?n_Distribution) WHERE { ?s a dcatapit:Distribution }")
          l.foreach((hm) => { logger.debug(hm.toString()) })
          l = kbrepo.execQuery("PREFIX dcatapit: <http://dati.gov.it/onto/dcatapit#> SELECT (count(?s) AS ?n_Catalog) WHERE { ?s a dcatapit:Catalog }")
          l.foreach((hm) => { logger.debug(hm.toString()) })
          l = kbrepo.execQuery("PREFIX dcatapit: <http://dati.gov.it/onto/dcatapit#> SELECT (count(?s) AS ?n_Standard) WHERE { ?s a dcatapit:Standard }")
          l.foreach((hm) => { logger.debug(hm.toString()) })
          l = kbrepo.execQuery("PREFIX dcatapit: <http://dati.gov.it/onto/dcatapit#> SELECT (count(?s) AS ?n_LicenseDocument) WHERE { ?s a dcatapit:LicenseDocument }")
          l.foreach((hm) => { logger.debug(hm.toString()) })

          logger.debug("-----------------------")

          //lancio le query di validazione
          val c = okValidators.find(v => v.getString("id").get.equalsIgnoreCase(validator)).get
          val path = dir_ontologies + "/" + c.getString("publisher").get.toLowerCase() + "/" + c.getString("ontology").get.toUpperCase() + "/validators/" + c.getString("type").get.toLowerCase();

          /**
           * TODO: vanno gestite meglio le eccezioni altrimenti ritorna sempre il messaggio
           * 			 "Impossibile caricare le regole" qualunque sia l'errore...
           */
          try {
            val fs = new FileDatastore(path)
            var nr: Int = 0;

            fs.listFile(1, "rq").foreach((i: URI) => {

              nr = nr + 1
              //if (nr==1) { //test query 0
              logger.debug(s"exec rq: [${i}]")
              var rq: String = "";

              val bufferedSource = Source.fromFile(i)
              for (line <- bufferedSource.getLines) {
                rq = rq + line + "\n\r"
              }

              bufferedSource.close

              val tl = kbrepo.execRuleQuery(rq);

              val pattern = "(.*)rule-([0-9]+).*".r
              val pattern(u, n) = i.toString()
              //logger.debug(s"[${u}][${n}]");
              if (tl.size > 0) logger.debug(s"rule : ${n} : [${tl.size}] --> [${tl(0).rule_severity}] ${tl(0).rule_description}")

              //concatenazione collection (Seq ++ Seq)
              det = det ++ tl.map(t => {
                ValidationDetail(
                  t.prd,
                  t.sbj, t.rule_message, t.obj,
                  t.rule_description,
                  t.class_name,
                  t.rule_id,
                  new ValidationDetailRuleSeverity(t.rule_severity))

              }).toSeq
              //}
            });

            //se ci sono dettagli la validazione ha avuto errori o warnings
            val n_errors = det.filter(d => d.ruleSeverity == new ValidationDetailRuleSeverity("error")).size
            val n_warnings = det.filter(d => d.ruleSeverity == new ValidationDetailRuleSeverity("warning")).size

            logger.debug(s"warnings: ${n_warnings}, errors: ${n_errors}")

            //cancello le triple
            kbrepo.clear(null)
            logger.debug(s"triples cleared from ${kbrepo}")
            logger.debug(s"triples found in ${kbrepo} : ${kbrepo.countTriples(null)}")

            val v_name = c.getString("name").get
            //ritorno il risultato
            if (n_errors > 0) {
              res = Validation(n_warnings, n_errors, s"Validazione completata con errori utilizzando il validatore #${v_name}", unixTimestamp, new ValidationResult("KO"), Option(det))
            } else {
              if (n_warnings > 0)
                res = Validation(n_warnings, n_errors, s"Validazione completata con segnalazioni utilizzando il validatore #${v_name}", unixTimestamp, new ValidationResult("OK"), Option(det))
              else {
                res = Validation(n_warnings, n_errors, s"Validazione completata con successo utilizzando il validatore #${v_name}", unixTimestamp, new ValidationResult("OK"), Option(det))
              }
            }

          } catch {
            case ex: Exception =>
              logger.error(s"cannot load rules from ${path}")
              //throw new RuntimeException(s"cannot load rules from ${path}", ex)
              res = Validation(0, 0, s"Impossibile caricare le regole dal path ${path}", unixTimestamp, new ValidationResult("KO"), Option(det))
          }

        } else { //errore nel caricamento delle ontologie

          //ritorno il risultato
          res = Validation(0, 0, loaded, unixTimestamp, new ValidationResult("KO"), Option(det))

        }

      } else { //errore nel caricamento del file da validare

        //ritorno il risultato
        res = Validation(0, 0, loaded, unixTimestamp, new ValidationResult("KO"), Option(det))

      }

    } else
      //ritorno il risultato
      res = Validation(0, 0, s"Validatore #${validator} non valido", unixTimestamp, new ValidationResult("KO"), Option(det))

    kbrepo.stop();
    res
  }

  @Inject
  def onStart(
    app:           Application,
    env:           Environment,
    configuration: Configuration)(implicit ec: ExecutionContext) {

    // get configs
    val app_type = configuration.underlying.getString("app.type")

    val data_dir = app_type match {
      case "dev"  => "./dist/data/ontologies"
      case "prod" => "./data/ontologies"
    }

    logger.debug(s"app_type: ${app_type}")
    logger.debug(s"data_dir: ${data_dir}")

    logger.info("ValidatorModuleBase START")

    // CHECK

    //verifica la presenza dei validatori configurati
    //    dir_ontologies = configuration.getString("validator.home").get

    var _conf = configuration.underlying
    _conf = ConfigHelper.injectParameters(_conf, ("validator.home", data_dir))
    dir_ontologies = _conf.getString("validator.home")
    logger.debug(dir_ontologies)

    val lv = configuration.getConfigList("validators").get
    //itera i validatori configurati
    lv.foreach((c) => {
      //cerca i file nella directory
      val path = dir_ontologies + "/" + c.getString("publisher").get.toLowerCase() + "/" + c.getString("ontology").get.toUpperCase() + "/validators/" + c.getString("type").get.toLowerCase();

      try {
        val fs = new FileDatastore(path)
        var nr: Int = 0;
        fs.listFile(1, "rq").foreach((i: URI) => {
          //logger.debug(s"[${i}]")
          nr = nr + 1
        });

        logger.debug(s"[${path}] : ${nr} rules")

        //aggiungo il validatore a quelli disponibili se ha almeno una regola
        if (nr > 0) {
          okValidators.append(c)
          logger.debug(s"appended [${okValidators.size}]")
        } else
          logger.debug(s"dropped : no rules")

      } catch {
        case ex: Exception =>
          logger.error(s"cannot load rules from ${path}")
        //throw new RuntimeException(s"cannot load rules from ${path}", ex)
      }
    })

    //itera i validatori configurati per caricare le ontologie
    lv.foreach((c) => {
      //cerca i file nella directory
      val path = dir_ontologies + "/" + c.getString("publisher").get.toLowerCase() + "/" + c.getString("ontology").get.toUpperCase()

      try {
        val fs = new FileDatastore(path)
        var nr: Int = 0;
        fs.listFile(1, "owl", "rdf", "ttl").foreach((i: URI) => {
          val f: File = new File(i)
          file_ontologies.append(f)
          logger.debug(s"appended ontology ${f.getName} [${file_ontologies.size}]")
        });

      } catch {
        case ex: Exception =>
          logger.error(s"cannot load ontology from ${path}")
        //throw new RuntimeException(s"cannot load rules from ${path}", ex)
      }
    })

  }

  // TODO:
  lifecycle.addStopHook({ () =>

    Future.successful {

      // this is useful for saving files, closing connection ,etc
      logger.info("ValidatorModuleBase STOP")

    }

  })

}

