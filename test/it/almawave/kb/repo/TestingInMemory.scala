package it.almawave.kb.repo

import specs.TestingBaseRDFRepository

class TestingInMemory extends TestingBaseRDFRepository {

  override val mock: RDFRepositoryBase = RDFRepository.memory()

}