package com.duoshouji.server.internal.user;

import junit.framework.Assert;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.user.RegisteredUser;
import com.duoshouji.server.user.UserCache;
import com.duoshouji.server.user.UserDao;

public class CachedUserRepositoryTest {
	
	private static final String MOCK_ACCOUNT_ID = "13661863279";
	
	private Mockery mockery;
	private UserDao userDao;
	private UserCache userCache;
	private RegisteredUser user;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		userDao = mockery.mock(UserDao.class);
		userCache = mockery.mock(UserCache.class);
		user = mockery.mock(RegisteredUser.class);
	}
	
	@Test
	public void findUserFromCache() {
		mockery.checking(new Expectations(){{
			oneOf(userCache).findUser(MOCK_ACCOUNT_ID); will(returnValue(user));
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userDao);
		Assert.assertEquals(user, userRepository.findUser(MOCK_ACCOUNT_ID));
	}
	
	@Test
	public void findUserFromDao() {
		mockery.checking(new Expectations(){{
			oneOf(userCache).findUser(MOCK_ACCOUNT_ID); will(returnValue(null));
			oneOf(userDao).findUser(MOCK_ACCOUNT_ID); will(returnValue(user));
			oneOf(userCache).putUser(user);
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userDao);
		Assert.assertEquals(user, userRepository.findUser(MOCK_ACCOUNT_ID));
	}
	
	@Test
	public void createNewUserWhenUserNotExists() {
		mockery.checking(new Expectations(){{
			oneOf(userCache).findUser(MOCK_ACCOUNT_ID); will(returnValue(null));
			oneOf(userDao).findUser(MOCK_ACCOUNT_ID); will(returnValue(null));
			oneOf(userCache).putUser(with(new UserWithMockAccountId()));
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userDao);
		Assert.assertEquals(user, userRepository.findUser(MOCK_ACCOUNT_ID));

	}

	private static class UserWithMockAccountId extends BaseMatcher<RegisteredUser> {

		@Override
		public boolean matches(Object user) {
			if (user == null)
				return false;
			if (!(user instanceof RegisteredUser))
				return false;
			RegisteredUser other = (RegisteredUser) user;
			return other.getIdentifier().equals(MOCK_ACCOUNT_ID);
		}

		@Override
		public void describeTo(Description arg0) {			
		}
	}
}
