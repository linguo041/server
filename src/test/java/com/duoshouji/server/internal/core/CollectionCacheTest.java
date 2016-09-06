package com.duoshouji.server.internal.core;

import org.junit.Assert;
import org.junit.Test;

import com.duoshouji.server.MockConstants;
import com.duoshouji.server.annotation.Collection;

public class CollectionCacheTest {

	@Test
	public void testRefreshAndNonrefresh() {
		CollectionCache cache = new CollectionCache();
		CounterInterface counter = new CounterClass();
		CounterInterface proxy = cache.getCollectionRequestor(MockConstants.MOCK_MOBILE_NUMBER, counter, true);
		Assert.assertEquals(1, proxy.getCollection(1).size());
		Assert.assertEquals(Integer.valueOf(0), proxy.getSize());
		
		((CounterClass)counter).addCount();
		proxy = cache.getCollectionRequestor(MockConstants.MOCK_MOBILE_NUMBER, counter, false);
		Assert.assertEquals(1, proxy.getCollection(1).size());
		Assert.assertEquals(3, proxy.getCollection(2).size());
		Assert.assertEquals(Integer.valueOf(1), proxy.getSize());
		
		proxy = cache.getCollectionRequestor(MockConstants.MOCK_MOBILE_NUMBER, counter, true);
		Assert.assertEquals(2, proxy.getCollection(1).size());
		Assert.assertEquals(Integer.valueOf(1), proxy.getSize());
	}

	@Test
	public void testFirstInvoke() {
		CollectionCache cache = new CollectionCache();
		CounterInterface counter = new CounterClass();
		CounterInterface proxy = cache.getCollectionRequestor(MockConstants.MOCK_MOBILE_NUMBER, counter, false);
		Assert.assertEquals(1, proxy.getCollection(1).size());
		Assert.assertEquals(Integer.valueOf(0), proxy.getSize());
	}

	@Collection
	public static interface MockCollection {
		int size();
	}
	
	public static interface CounterInterface {
		Integer getSize();
		MockCollection getCollection(int add);
	}
	
	public static class CounterClass implements CounterInterface {
		private int counter = 0;
		
		@Override
		public Integer getSize() {
			return Integer.valueOf(counter);
		}

		void addCount() {
			++counter;
		}
		
		@Override
		public MockCollection getCollection(final int add) {
			return new MockCollection(){
				final int size = counter;
				@Override
				public int size() {
					return size + add;
				}
			};
		}
		
	}
}
