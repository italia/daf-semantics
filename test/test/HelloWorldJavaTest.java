package test;

import org.junit.Assert;
import org.junit.Test;

// a simple test to check the configuration of Junit in Play!
public class HelloWorldJavaTest {

	@Test
	public void testSimple() {
		int a = 1 + 1;
		Assert.assertEquals(a, 2);
	}

}