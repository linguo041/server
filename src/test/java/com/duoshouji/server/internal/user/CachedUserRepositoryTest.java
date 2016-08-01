package com.duoshouji.server.internal.user;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.MockConstants;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserCache;
import com.duoshouji.server.service.user.UserRepository;

public class CachedUserRepositoryTest {
		
	private Mockery mockery;
	private UserCache userCache;
	private RegisteredUser user;
	private UserRepository delegator;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		userCache = mockery.mock(UserCache.class);
		user = mockery.mock(RegisteredUser.class);
		delegator = mockery.mock(UserRepository.class);
	}
	
	@Test
	public void findUserFromCache() {
		mockery.checking(new Expectations(){{
			oneOf(userCache).findUser(MockConstants.MOCK_USER_IDENTIFIER); will(returnValue(user));
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userCache, delegator);
		Assert.assertEquals(user, userRepository.findUser(MockConstants.MOCK_MOBILE_NUMBER));
	}
	
	@Test
	public void findUserFromDelegator() {
		mockery.checking(new Expectations(){{
			oneOf(userCache).findUser(MockConstants.MOCK_USER_IDENTIFIER); will(returnValue(null));
			oneOf(delegator).findUser(MockConstants.MOCK_USER_IDENTIFIER); will(returnValue(user));
			oneOf(userCache).putUser(user);
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userCache, delegator);
		Assert.assertEquals(user, userRepository.findUser(MockConstants.MOCK_MOBILE_NUMBER));
	}
	
	@Test
	public void createUser() {
		mockery.checking(new Expectations(){{
			oneOf(delegator).createUser(MockConstants.MOCK_MOBILE_NUMBER); will(returnValue(user));
			oneOf(userCache).putUser(user);
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userCache, delegator);
		Assert.assertEquals(user, userRepository.createUser(MockConstants.MOCK_MOBILE_NUMBER));
	}
}
