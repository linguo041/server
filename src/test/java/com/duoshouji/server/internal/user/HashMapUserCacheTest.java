package com.duoshouji.server.internal.user;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.MockConstants;
import com.duoshouji.server.service.user.RegisteredUser;

public class HashMapUserCacheTest {

	private Mockery mockery;
	private RegisteredUser mockUser;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		mockUser = mockery.mock(RegisteredUser.class);
	}
	
	@Test
	public void getExistingUser() {
		mockery.checking(new Expectations(){{
			oneOf(mockUser).getIdentifier(); will(returnValue(MockConstants.MOCK_USER_IDENTIFIER));
		}});
		
		HashMapUserCache cache = new HashMapUserCache();
		cache.putUser(mockUser);
		Assert.assertEquals(mockUser, cache.findUser(MockConstants.MOCK_USER_IDENTIFIER));
	}
	
	@Test
	public void getNonExistingUser() {
		HashMapUserCache cache = new HashMapUserCache();
		Assert.assertNull(cache.findUser(MockConstants.MOCK_USER_IDENTIFIER));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void invokeFindUserWithNullInput() {
		HashMapUserCache cache = new HashMapUserCache();
		cache.findUser(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void invokePutUserWithNullInput() {
		HashMapUserCache cache = new HashMapUserCache();
		cache.putUser(null);
	}
	
}
