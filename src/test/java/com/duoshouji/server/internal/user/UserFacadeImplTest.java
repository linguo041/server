package com.duoshouji.server.internal.user;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.MockConstants;
import com.duoshouji.server.internal.core.DuoShouJiFacadeImpl;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserRepository;

public class UserFacadeImplTest {

	private Mockery mockery;
	private UserRepository userRepository;
	private RegisteredUser user;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		userRepository = mockery.mock(UserRepository.class);
		user = mockery.mock(RegisteredUser.class);
	}
	
	@Test
	public void getExistingUser() {
		mockery.checking(new Expectations(){{
			oneOf(userRepository).findUser(MockConstants.MOCK_MOBILE_NUMBER); will(returnValue(user));
		}});
		
		DuoShouJiFacadeImpl userFacade = new DuoShouJiFacadeImpl(userRepository);
		Assert.assertEquals(user, userFacade.getUser(MockConstants.MOCK_MOBILE_NUMBER));
	}
	
	@Test
	public void createNewUserWhenGettingNonExistingUser() {
		mockery.checking(new Expectations(){{
			oneOf(userRepository).findUser(MockConstants.MOCK_MOBILE_NUMBER); will(returnValue(null));
			oneOf(userRepository).createUser(MockConstants.MOCK_MOBILE_NUMBER); will(returnValue(user));
		}});
		
		DuoShouJiFacadeImpl userFacade = new DuoShouJiFacadeImpl(userRepository);
		Assert.assertEquals(user, userFacade.getUser(MockConstants.MOCK_MOBILE_NUMBER));

	}
}
