package repositories

import java.nio.file.Paths

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.setAsJavaSet
import org.eclipse.rdf4j.model.ValueFactory
import org.eclipse.rdf4j.model.impl.SimpleValueFactory
import org.eclipse.rdf4j.repository.Repository
import org.eclipse.rdf4j.repository.sail.SailRepository
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository
import org.eclipse.rdf4j.sail.memory.MemoryStore
import org.eclipse.rdf4j.query.BindingSet;
import play.api.Logger
import org.slf4j.LoggerFactory
import org.eclipse.rdf4j.repository.RepositoryConnection
import java.io.File
import java.io.FileInputStream
import org.eclipse.rdf4j.rio.RDFFormat
import org.eclipse.rdf4j.repository.RepositoryResult
import java.io.InputStream
import org.eclipse.rdf4j.model.Statement
import views.html.helper.input
import org.eclipse.rdf4j.query.TupleQuery
import java.net.URLDecoder
import org.eclipse.rdf4j.rio.Rio
import org.eclipse.rdf4j.query.BindingSet
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import org.eclipse.rdf4j.query.TupleQueryResultHandler
import org.eclipse.rdf4j.model.Literal
import org.eclipse.rdf4j.sail.memory.model.MemIRI
import org.eclipse.rdf4j.model.impl.SimpleIRI
import org.eclipse.rdf4j.model.Resource
import org.eclipse.rdf4j.model.IRI
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.CharSequenceReader
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException
import java.io.IOException
import org.eclipse.rdf4j.rio.RDFParseException
import org.eclipse.rdf4j.sail.spin.SpinSail
import org.eclipse.rdf4j.sail.inferencer.fc.ForwardChainingRDFSInferencer
import org.eclipse.rdf4j.sail.inferencer.fc.DedupingInferencer
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer


//singleton (object)
object RDFRepository {

  val logger = Logger.underlyingLogger

  def remote(endpoint: String) = {
    new RDFRepositoryBase(new SPARQLRepository(endpoint, endpoint))
  }

  def memory(rdfsinf : Boolean) = {
     
    //Repository in memoria con "full RDFS reasoning"
    val mem = new MemoryStore
    var repo : SailRepository = null
    if (rdfsinf)
      repo =  new SailRepository(new ForwardChainingRDFSInferencer(new DedupingInferencer(mem)))
    else
     repo = new SailRepository(mem)
      
    new RDFRepositoryBase(repo)
      
    //val repo = new SailRepository(mem)
    //val repo = new SailRepository(new DedupingInferencer(mem))
    //val repo = new SailRepository(new ForwardChainingRDFSInferencer(mem))
    
  }

  def memory(dir_cache: String) = {

    val dataDir = Paths.get(dir_cache).normalize().toAbsolutePath().toFile()
    if (!dataDir.exists()) dataDir.mkdirs()
    val mem = new MemoryStore()
    mem.setDataDir(dataDir)
    mem.setSyncDelay(1000L)
    mem.setPersist(false)
    mem.setConnectionTimeOut(1000)
    val repo = new SailRepository(mem)
    new RDFRepositoryBase(repo)

  }

}

class RDFRepositoryBase(repo: Repository) {

  var vf: ValueFactory = SimpleValueFactory.getInstance
  val logger = Logger.underlyingLogger
  var conn: RepositoryConnection = null

  def loadRDF(rdfName: String, rdfFile: File, ctx: Resource) : String = {

    //load ok
    var msg = "OK"
    
    val default_format = RDFFormat.TURTLE
    val format = Rio.getParserFormatForFileName(rdfName).orElse(default_format)
    
    // Open a connection to the database
    try {

      conn = repo.getConnection
      try {

        val input: InputStream = new FileInputStream(rdfFile.getAbsoluteFile)
        
        val doc = Rio.parse(input, "", format, ctx)
        conn.add(doc, ctx);

        input.close()

      } catch {
        case ex: IOException =>
          //ex.printStackTrace()
          msg = s"error attempting load file: ${ex.getMessage}"
          logger.error(msg)
          
         case ex: RDFParseException =>
           //ex.printStackTrace()
           msg = s"error attempting parse file: ${ex.getMessage}"
           logger.error(msg)
           
         case ex: UnsupportedRDFormatException =>
           //ex.printStackTrace()
           msg = s"error attempting read file format: ${ex.getMessage}"
           logger.error(msg)  
           
         case ex: Exception => 
           //ex.printStackTrace()
           msg = s"error attempting load / parse file: ${ex.getMessage}"
           logger.error(msg)
      }

    } catch {
      case ex: Exception => 
        //ex.printStackTrace()
        msg = s"error attempting connection to repository: ${ex.getMessage}"
        logger.error(msg)
    } finally {
      // chiudo la connessione
      if (conn != null) conn.close()
    }

    //ritorna il messaggio
    msg
    
  }

  
  def countTriples(contexts: Resource*): Long =  {

    var size : Long = -1;
    // Open a connection to the database
    try {

      conn = repo.getConnection

      //esegue il conteggio
      try {

          size = conn.size(contexts: _*) // CHECK: blank nodes!
          
      } catch {
        case ex: Exception =>
          ex.printStackTrace()
          logger.error(s"error attempting count triples")

      }
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        logger.error(s"error attempting connection to repository")
    } finally {
      // chiudo la connessione
      if (conn != null) conn.close()

    }
    
    size
    
  }
  
  def contexts(): Seq[String] = {

      val conn = repo.getConnection

      val results: Seq[String] = conn.getContextIDs.map { ctx => ctx.stringValue() }.toList
      
      conn.getContextIDs
      
      conn.close()

      results
      
   }
  
  //struttura del risultato di una query di validazione
  case class resQueryRow (class_name : String , rule_id : Int, rule_severity : String, rule_description : String, rule_message : String, sbj : String, prd : String, obj : String) 
  
  def execRuleQuery(rq : String) :  List[resQueryRow] = {
        
    var res = List[resQueryRow]()
    // Open a connection to the database
    try {

      conn = repo.getConnection

      //esegue la regola (sparql)
      try {
        
        //logger.debug(rq)
        
        /**
         * inserisco il contesto nella query, vale per tutte le query del tipo
         * SELECT ... WHERE { ... }
         * già per una UNION il contesto viene messo solo sulla select più esterna 
         * da verificare se va bene anche in casi più complessi
        val r = "((w)|(W))((h)|(H))((e)|(E))((r)|(R))((e)|(E))\\s*\\{".r
        var rq_new = r.replaceFirstIn(rq,"WHERE { GRAPH <"+ctx.toString() +"> {")
        rq_new = rq_new.patch(rq_new.lastIndexOf('}'), "}}", 1)
        //logger.debug("###"+ rq_new)
       	*/
        
        val query: TupleQuery = conn.prepareTupleQuery(rq)
        //query.setIncludeInferred(true)
        
        val result = query.evaluate()
        while (result.hasNext()) {
          
          val bindingSet = result.next()
          
          /*
          logger.debug("Class_Name       :::: "+ bindingSet.getValue("Class_Name"))
          logger.debug("Rule_ID          :::: "+ bindingSet.getValue("Rule_ID"))
          logger.debug("Rule_Severity    :::: "+ bindingSet.getValue("Rule_Severity"))
          logger.debug("Rule_Description :::: "+ bindingSet.getValue("Rule_Description"))
          logger.debug("Message          :::: "+ bindingSet.getValue("Message"))
          logger.debug("s                :::: "+ bindingSet.getValue("s"))
          logger.debug("p                :::: "+ bindingSet.getValue("p"))
          logger.debug("o                :::: "+ bindingSet.getValue("o"))
          */
          
          val class_name = bindingSet.getValue("Class_Name").asInstanceOf[Literal].stringValue()
          val rule_id = bindingSet.getValue("Rule_ID").asInstanceOf[Literal].intValue()
          val rule_severity = bindingSet.getValue("Rule_Severity").asInstanceOf[Literal].stringValue()
          val rule_description = bindingSet.getValue("Rule_Description").asInstanceOf[Literal].stringValue()
          val rule_message = bindingSet.getValue("Message").asInstanceOf[Literal].stringValue()
          val s = bindingSet.getValue("s") match  {                                                
                                              case a:Object => a.toString()
                                              case null => ""
                                           };
          val p = bindingSet.getValue("p") match  {                                                
                                              case a:Object => a.toString()
                                              case null => ""
                                           };
          val o = bindingSet.getValue("o") match  {                                                
                                              case a:Object => a.toString()
                                              case null => ""
                                           };
          
          //logger.debug(s"${class_name} | ${rule_id} | ${rule_severity} | ${rule_description} | ${s} | ${p} | ${o}")
                                            
          res = res:+(resQueryRow(class_name, rule_id, rule_severity, rule_description, rule_message, s, p, o))
           
        }
        
        result.close()
        
      } catch {
        case ex: Exception =>
          ex.printStackTrace()
          logger.error(s"error attempting browse result")
          
      }
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        logger.error(s"error attempting connection to repository")
    } finally {
      // chiudo la connessione
      
      if (conn != null) conn.close()
    }
    
    res
  }

  def execQuery(rq : String) :  ListBuffer[HashMap[String,String]] = {
        
    var res = ListBuffer[HashMap[String,String]]()
    // Open a connection to the database
    try {

      conn = repo.getConnection

      //esegue la regola (sparql)
      try {
        
        val query: TupleQuery = conn.prepareTupleQuery(rq)
        
        val result = query.evaluate()
        while (result.hasNext()) {
          
          val bindingSet = result.next();
          val hm : HashMap[String, String] = new HashMap
          bindingSet.getBindingNames.foreach((n:String) => {
             hm.put(n,bindingSet.getValue(n).stringValue())
          })
          res+=(hm)
        }
        
        result.close()
        
      } catch {
        case ex: Exception =>
          ex.printStackTrace()
          logger.error(s"error attempting browse result")
          
      }
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        logger.error(s"error attempting connection to repository")
    } finally {
      // chiudo la connessione
      
      if (conn != null) conn.close()
    }
    
    res
  }
  
  
  /**
   * TEST
   */
  def test(dir:String) {

      val context = "http://validator/context/test"
      val ctx = SimpleValueFactory.getInstance.createIRI(context.trim())
      val fs = new File(dir+"/../test/test.ttl")
      loadRDF("test.ttl", fs, null)
      val l : ListBuffer[HashMap[String,String]] = execQuery("prefix ex: <http://localhost/> SELECT ?s where  { ?s a ex:TelephoneType} ")
      l.foreach ((hm) => {
          logger.debug(hm.toString())
      })
  }  
  
  def clear(contexts: Resource*) {

      val conn = repo.getConnection
      conn.begin()

      try {
        conn.clear(contexts: _*)
        conn.commit()
      } catch {
        case ex: Exception =>
          logger.error(s"cannot clear contexts: ${contexts.mkString(", ")}")
          conn.rollback()
      }

      conn.close()
      
    }
  
  def start() {
    if (!repo.isInitialized())
      repo.initialize()

    // checking if the repository is up. TODO: refactoring to a method
    try {
      val _conn = repo.getConnection
      logger.debug("connecting...")
      _conn.close()
      logger.debug("done.")
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        logger.error(s"error attempting connection to repository") // TODO: config with url
        System.exit(1) // cannot start application!
    }

    vf = repo.getValueFactory

    logger.info("RDFRepositoryBase strated")
    
  }

  def stop() {
    if (repo.isInitialized())
      repo.shutDown()

    logger.info("RDFRepositoryBase stopped")

  }
  
  /**
   * classe HELPER implicit che wrappa RepositoryResultIterator e gli fa estendere iterator
   * aggiungendo i metodi hasNext e next in modo da poter usare .map su conn.getContextIDs
   */
  implicit class RepositoryResultIterator[T](result: RepositoryResult[T]) extends Iterator[T] {
    def hasNext: Boolean = result.hasNext()
    def next(): T = result.next()
  }

}
  
 