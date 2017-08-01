package it.almawave.kb.repo

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Assume

class TestingVirtuoso extends TestingBaseRDFRepository {

  // NOTE: this test should be executed only if a virtuoso instance is actually up and running
  override val mock: RDFRepositoryBase = RDFRepository.virtuoso()

}