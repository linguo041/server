package com.duoshouji.server.internal.user;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.MockConstants;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.RegisteredUserDto;
import com.duoshouji.server.service.user.UserAlreadyExistsException;
import com.duoshouji.server.service.user.UserCache;
import com.duoshouji.server.service.user.UserDao;
import com.duoshouji.server.util.MessageProxyFactory;
import com.duoshouji.server.util.VerificationCodeGenerator;

import junit.framework.Assert;

public class CachedUserRepositoryTest {
		
	private Mockery mockery;
	private UserDao userDao;
	private UserCache userCache;
	private RegisteredUser user;
	private UserOperationManager operationManager;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		userDao = mockery.mock(UserDao.class);
		userCache = mockery.mock(UserCache.class);
		user = mockery.mock(RegisteredUser.class);
		operationManager = new UserOperationManager(
				mockery.mock(VerificationCodeGenerator.class)
				, mockery.mock(MessageProxyFactory.class));
	}
	
	@Test
	public void findUserFromCache() {
		mockery.checking(new Expectations(){{
			oneOf(userCache).findUser(MockConstants.MOCK_USER_IDENTIFIER); will(returnValue(user));
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userDao, userCache, operationManager);
		Assert.assertEquals(user, userRepository.findUser(MockConstants.MOCK_MOBILE_NUMBER));
	}
	
	@Test
	public void findUserFromDao() {
		mockery.checking(new Expectations(){{
			oneOf(userCache).findUser(MockConstants.MOCK_USER_IDENTIFIER); will(returnValue(null));
			oneOf(userDao).findUser(MockConstants.MOCK_USER_IDENTIFIER); will(returnValue(mockery.mock(RegisteredUserDto.class)));
			oneOf(userCache).putUser(with(any(RegisteredUser.class)));
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userDao, userCache, operationManager);
		Assert.assertNotNull(userRepository.findUser(MockConstants.MOCK_MOBILE_NUMBER));
	}
	
	@Test
	public void createUser() {
		mockery.checking(new Expectations(){{
			allowing(userCache).findUser(MockConstants.MOCK_USER_IDENTIFIER); will(returnValue(null));
			allowing(userDao).findUser(MockConstants.MOCK_USER_IDENTIFIER); will(returnValue(null));
			oneOf(userDao).addUser(MockConstants.MOCK_USER_IDENTIFIER, MockConstants.MOCK_MOBILE_NUMBER);
			oneOf(userCache).putUser(with(any(RegisteredUser.class)));
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userDao, userCache, operationManager);
		userRepository.createUser(MockConstants.MOCK_MOBILE_NUMBER);
	}
	
	@Test(expected=UserAlreadyExistsException.class)
	public void createUserWithExistingUserIdentifier1() {
		mockery.checking(new Expectations(){{
			allowing(userCache).findUser(MockConstants.MOCK_USER_IDENTIFIER); will(returnValue(null));
			allowing(userDao).findUser(MockConstants.MOCK_USER_IDENTIFIER);
			will(returnValue(new InMemoryRegisteredUserDto(MockConstants.MOCK_USER_IDENTIFIER, MockConstants.MOCK_MOBILE_NUMBER)));
			allowing(userCache).putUser(with(any(RegisteredUser.class)));
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userDao, userCache,operationManager);
		userRepository.createUser(MockConstants.MOCK_MOBILE_NUMBER);
	}

	@Test(expected=UserAlreadyExistsException.class)
	public void createUserWithExistingUserIdentifier2() {
		mockery.checking(new Expectations(){{
			allowing(userCache).findUser(MockConstants.MOCK_USER_IDENTIFIER); will(returnValue(user));
		}});
		CachedUserRepository userRepository = new CachedUserRepository(userDao, userCache, operationManager);
		userRepository.createUser(MockConstants.MOCK_MOBILE_NUMBER);
	}
}
