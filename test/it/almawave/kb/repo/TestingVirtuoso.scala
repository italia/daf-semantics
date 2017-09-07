package it.almawave.kb.repo

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Assume
import org.junit.Ignore
import scala.util.Try
import it.almawave.kb.utils.ServerChecker
import org.slf4j.LoggerFactory

//@Ignore
class TestingVirtuoso extends TestingBaseRDFRepository {

  // NOTE: this test should be executed only if a virtuoso instance is actually up and running
  override val mock: RDFRepositoryBase = RDFRepository.virtuoso()

}

object TestingVirtuoso {

  val logger = LoggerFactory.getLogger(this.getClass)

  @BeforeClass
  def check_before() {

    val virtuoso_up = ServerChecker.isListening("localhost", 1111)
    Assume.assumeTrue(virtuoso_up)

    logger.info("Virtuoso is UP! [TESTING...]")

  }

}

