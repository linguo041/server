package com.duoshouji.restapi.annotation;

import org.junit.Assert;
import org.junit.Test;

import com.duoshouji.service.annotation.Unique;

public class ReflectionTest {

	
	@Test
	public void testAnnotationInhertent() {
		Assert.assertTrue(TestInterface.class.isAnnotationPresent(Unique.class));
		Assert.assertFalse(TestClass.class.isAnnotationPresent(Unique.class));
	}
	
	@Test
	public void testGetIntefaces() {
		Class<?>[] interfaces = TestClass.class.getInterfaces();
		Assert.assertEquals(1, interfaces.length);
		Assert.assertSame(TestInterface.class, interfaces[0]);
	}
	
	private interface TestRootInterface {}
	
	@Unique
	private interface TestInterface extends TestRootInterface {}

	@Unique
	private static class TestSuperClass {}
	
	private static class TestClass extends TestSuperClass implements TestInterface {}
	
}
