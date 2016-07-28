package com.duoshouji.server.internal.user;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.user.RegisteredUser;
import com.duoshouji.server.user.UserAlreadyExistsException;
import com.duoshouji.server.user.UserCache;
import com.duoshouji.server.user.UserDao;
import com.duoshouji.server.user.UserIdentifier;

import junit.framework.Assert;

public class CachedUserRepositoryTest {
	
	private static final UserIdentifier MOCK_ACCOUNT_ID = new UserIdentifier("13661863279");
	
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
		CachedUserRepository userRepository = new CachedUserRepository(userDao, userCache);
		Assert.assertEquals(user, userRepository.findUser(MOCK_ACCOUNT_ID));
	}
	
	@Test
	public void findUserFromDao() {
		mockery.checking(new Expectations(){{
			oneOf(userCache).findUser(MOCK_ACCOUNT_ID); will(returnValue(null));
			oneOf(userDao).findUser(MOCK_ACCOUNT_ID); will(returnValue(user));
			oneOf(userCache).putUser(user);
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userDao, userCache);
		Assert.assertEquals(user, userRepository.findUser(MOCK_ACCOUNT_ID));
	}
	
	@Test
	public void createUser() {
		mockery.checking(new Expectations(){{
			allowing(userCache).findUser(MOCK_ACCOUNT_ID); will(returnValue(null));
			allowing(userDao).findUser(MOCK_ACCOUNT_ID); will(returnValue(null));
			oneOf(userDao).addUser(with(any(RegisteredUser.class)));
			oneOf(userCache).putUser(with(any(RegisteredUser.class)));
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userDao, userCache);
		userRepository.createUser(MOCK_ACCOUNT_ID);
	}
	
	@Test(expected=UserAlreadyExistsException.class)
	public void createUserWithExistingUserIdentifier1() {
		mockery.checking(new Expectations(){{
			allowing(userCache).findUser(MOCK_ACCOUNT_ID); will(returnValue(null));
			allowing(userDao).findUser(MOCK_ACCOUNT_ID); will(returnValue(user));
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userDao, userCache);
		userRepository.createUser(MOCK_ACCOUNT_ID);
	}

	@Test(expected=UserAlreadyExistsException.class)
	public void createUserWithExistingUserIdentifier2() {
		mockery.checking(new Expectations(){{
			allowing(userCache).findUser(MOCK_ACCOUNT_ID); will(returnValue(user));
			allowing(userDao).findUser(MOCK_ACCOUNT_ID); will(returnValue(null));
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userDao, userCache);
		userRepository.createUser(MOCK_ACCOUNT_ID);
	}
}
