package com.duoshouji.server.internal.core;

import org.junit.Assert;
import org.junit.Test;

import com.duoshouji.server.annotation.Unique;

public class HashMapUniqueObjectCacheTest {

	private static final Long LONG_KEY_1 = Long.valueOf(1);
	private static final Long LONG_KEY_2 = Long.valueOf(2);
	private static final String STRING_KEY = "ANY_MOCK_STRING";
	
	@Test
	public void differntIdentifierAndDifferentType() {
		HashMapUniqueObjectCache target = new HashMapUniqueObjectCache();
		target.put(STRING_KEY, TestClass4.INSTANCE);
		target.put(LONG_KEY_1, TestClass1.INSTANCE);
		
		Assert.assertSame(TestClass4.INSTANCE, target.get(STRING_KEY, TestClass4.class));
		Assert.assertSame(TestClass1.INSTANCE, target.get(LONG_KEY_1, TestInterface.class));
	}

	@Test
	public void differntIdentifierAndSameType() {
		HashMapUniqueObjectCache target = new HashMapUniqueObjectCache();
		target.put(LONG_KEY_1, TestClass1.INSTANCE);
		target.put(LONG_KEY_2, TestClass2.INSTANCE);
		
		Assert.assertSame(TestClass1.INSTANCE, target.get(LONG_KEY_1, TestInterface.class));
		Assert.assertSame(TestClass2.INSTANCE, target.get(LONG_KEY_2, TestInterface.class));
	}

	@Test
	public void sameIdentifierAndSameType() {
		HashMapUniqueObjectCache target = new HashMapUniqueObjectCache();
		target.put(LONG_KEY_1, TestClass1.INSTANCE);
		target.put(LONG_KEY_1, TestClass2.INSTANCE);
		
		Assert.assertSame(TestClass2.INSTANCE, target.get(LONG_KEY_1, TestInterface.class));
	}
	
	@Test
	public void sameIdentifierAndExtendedType() {
		HashMapUniqueObjectCache target = new HashMapUniqueObjectCache();
		target.put(LONG_KEY_1, TestClass3.INSTANCE);
		target.put(LONG_KEY_1, TestClass1.INSTANCE);
		
		Assert.assertSame(TestClass1.INSTANCE, target.get(LONG_KEY_1, TestInterface.class));
	}
	
	@Unique
	public static interface TestInterface {
	}
	
	public static class TestClass1 implements TestInterface {
		static final TestClass1 INSTANCE = new TestClass1();
	}
	
	public static class TestClass2 implements TestInterface {
		static final TestClass2 INSTANCE = new TestClass2();
	}
	
	public static class TestClass3 extends TestClass1 {
		static final TestClass3 INSTANCE = new TestClass3();
	}

	@Unique
	public static class TestClass4 {
		static final TestClass4 INSTANCE = new TestClass4();
	}
}
