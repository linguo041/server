package com.duoshouji.server.internal.login;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.MockConstants;
import com.duoshouji.server.service.DuoShouJiFacade;
import com.duoshouji.server.service.user.AccountSecurity;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.verify.VerificationCodeLoginExecutor;

import junit.framework.Assert;

public class LoginFacadeImplTest {
	
	private Mockery mockery;
	private DuoShouJiFacade userFacade;
	private RegisteredUser user;
	private VerificationCodeLoginExecutor loginExecutor;
	private AccountSecurity accountSecurity;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		userFacade = mockery.mock(DuoShouJiFacade.class);
		user = mockery.mock(RegisteredUser.class);
		loginExecutor = mockery.mock(VerificationCodeLoginExecutor.class);
		accountSecurity = mockery.mock(AccountSecurity.class);
	}
	
	@Test
	public void sendVerificationCode() {
		mockery.checking(new Expectations(){{
			oneOf(userFacade).getUser(MockConstants.MOCK_MOBILE_NUMBER); will(returnValue(user));
			oneOf(user).processVerificationCodeLogin(); will(returnValue(loginExecutor));
			oneOf(loginExecutor).sendVerificationCode();
		}});
		
		LoginFacadeImpl loginFacade  = new LoginFacadeImpl(userFacade);
		loginFacade.sendLoginVerificationCode(MockConstants.MOCK_MOBILE_NUMBER);
	}
	
	@Test
	public void checkVerificationCode() {
		mockery.checking(new Expectations(){{
			oneOf(userFacade).getUser(MockConstants.MOCK_MOBILE_NUMBER); will(returnValue(user));
			oneOf(user).processVerificationCodeLogin(); will(returnValue(loginExecutor));
			oneOf(loginExecutor).authenticate(MockConstants.MOCK_VERIFICATION_CODE); will(returnValue(true));
		}});
		
		LoginFacadeImpl loginFacade  = new LoginFacadeImpl(userFacade);
		Assert.assertEquals(user, loginFacade.verificationCodeLogin(MockConstants.MOCK_MOBILE_NUMBER, MockConstants.MOCK_VERIFICATION_CODE));
	}
	
	@Test
	public void verifyPassword() {
		mockery.checking(new Expectations(){{
			oneOf(userFacade).getUser(MockConstants.MOCK_MOBILE_NUMBER); will(returnValue(user));
			allowing(user).getAccountSecurity(); will(returnValue(accountSecurity));
			atLeast(1).of(accountSecurity).hasPassword(); will(returnValue(true));
			oneOf(accountSecurity).verifyPassword(MockConstants.MOCK_PASSWORD); will(returnValue(true));
		}});
		
		LoginFacadeImpl loginFacade  = new LoginFacadeImpl(userFacade);
		Assert.assertEquals(user, loginFacade.passwordLogin(MockConstants.MOCK_MOBILE_NUMBER, MockConstants.MOCK_PASSWORD));
	}
}
