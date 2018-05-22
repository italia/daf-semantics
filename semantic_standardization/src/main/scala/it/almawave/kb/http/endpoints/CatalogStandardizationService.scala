package it.almawave.kb.http.endpoints

import javax.inject.Singleton
import javax.ws.rs.Path
import org.slf4j.LoggerFactory
import it.almawave.kb.http.models.OntologyMetaModel
import com.typesafe.config.ConfigFactory
import java.nio.file.Paths
import it.almawave.linkeddata.kb.catalog.CatalogBox
import it.almawave.linkeddata.kb.utils.JSONHelper
import it.almawave.daf.standardization.refactoring.CatalogStandardizer

@Singleton
@Path("conf://api-catalog-config")
class CatalogStandardizationService {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val conf = ConfigFactory.parseFile(Paths.get("./conf/catalog.conf").normalize().toFile())
  val catalog = new CatalogBox(conf)
  catalog.start()

  val _standardizer = CatalogStandardizer(catalog)
  _standardizer.start

  def stardardizer = _standardizer

  //  TODO: STOP?

}
