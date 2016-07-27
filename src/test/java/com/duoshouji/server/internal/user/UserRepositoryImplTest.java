package com.duoshouji.server.internal.user;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.user.RegisteredUser;
import com.duoshouji.server.user.UserDao;
import com.duoshouji.server.user.UserRepository;

import junit.framework.Assert;

public class UserRepositoryImplTest {
	
	private static final String MOCK_ACCOUNT_ID = "13661863279";
	
	private Mockery mockery;
	private UserDao userDao;
	private RegisteredUser user;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		userDao = mockery.mock(UserDao.class);
		user = mockery.mock(RegisteredUser.class);
	}
	
	@Test
	public void testFindUser() {
		mockery.checking(new Expectations(){{
			oneOf(userDao).findUser(MOCK_ACCOUNT_ID); will(returnValue(user));
		}});
		UserRepositoryImpl userRepository = new UserRepositoryImpl(userDao);
		Assert.assertEquals(user, userRepository.findUser(MOCK_ACCOUNT_ID));
	}

}
