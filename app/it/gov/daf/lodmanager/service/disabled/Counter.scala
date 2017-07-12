package it.gov.daf.lodmanager.service.disabled

import java.util.concurrent.atomic.AtomicInteger
import javax.inject._

// DUMMY TEST

trait Counter {
  def next(): Int
}

@Singleton
class AtomicCounter extends Counter {
  private val atomicCounter = new AtomicInteger()
  override def next(): Int = atomicCounter.getAndIncrement()
}