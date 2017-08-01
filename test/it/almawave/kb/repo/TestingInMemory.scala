package it.almawave.kb.repo

class TestingInMemory extends TestingBaseRDFRepository {
  override val mock: RDFRepositoryBase = RDFRepository.memory()
  //  var mock = RDFRepository.virtuoso()
}