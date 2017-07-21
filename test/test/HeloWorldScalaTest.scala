package test

import org.junit.Assert
import org.junit.Test

// a simple test to check the configuration of Junit in Play!
class HelloWorldScalaTest {

  @Test
  def testSimple() {
    val a = 1 + 1
    Assert.assertEquals(a, 2)
  }

}