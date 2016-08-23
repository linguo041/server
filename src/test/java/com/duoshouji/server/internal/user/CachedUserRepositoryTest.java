package com.duoshouji.server.internal.user;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.MockConstants;
import com.duoshouji.server.internal.core.CachedUserRepository;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.MobileNumber;

public class CachedUserRepositoryTest {
		
	private Mockery mockery;
	private RegisteredUser user;
	private UserRepository delegator;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		user = mockery.mock(RegisteredUser.class);
		delegator = mockery.mock(UserRepository.class);
	}
	
	@Test
	public void findUserFromDelegator() {
		mockery.checking(new Expectations(){{
			oneOf(delegator).getUser(MockConstants.MOCK_USER_IDENTIFIER); will(returnValue(user));
			oneOf(user).getIdentifier(); will(returnValue(MockConstants.MOCK_USER_IDENTIFIER));
		}});
		CachedUserRepository userRepository = new CachedUserRepository(delegator);
		Assert.assertEquals(user, userRepository.getUser(MockConstants.MOCK_MOBILE_NUMBER));
	}
	
	@Test
	public void findUserFromCache() {
		mockery.checking(new Expectations(){{
			oneOf(delegator).getUser(MockConstants.MOCK_USER_IDENTIFIER); will(returnValue(user));
			oneOf(user).getIdentifier(); will(returnValue(MockConstants.MOCK_USER_IDENTIFIER));
		}});
		CachedUserRepository userRepository = new CachedUserRepository(delegator);
		Assert.assertEquals(user, userRepository.getUser(MockConstants.MOCK_MOBILE_NUMBER));
		Assert.assertEquals(user, userRepository.getUser(MockConstants.MOCK_MOBILE_NUMBER));
	}
	
	@Test
	public void createUser() {
		mockery.checking(new Expectations(){{
			oneOf(delegator).createUser(MockConstants.MOCK_MOBILE_NUMBER); will(returnValue(user));
			oneOf(user).getIdentifier(); will(returnValue(MockConstants.MOCK_USER_IDENTIFIER));
		}});
		CachedUserRepository userRepository = new CachedUserRepository(delegator);
		Assert.assertEquals(user, userRepository.createUser(MockConstants.MOCK_MOBILE_NUMBER));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void throwExceptionForNullInput1() {
		new CachedUserRepository(delegator).getUser((MobileNumber)null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void throwExceptionForNullInput2() {
		new CachedUserRepository(delegator).getUser((UserIdentifier)null);
	}

}
