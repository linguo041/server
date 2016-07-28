package com.duoshouji.server.internal.login;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.user.AccountSecurity;
import com.duoshouji.server.user.PasswordNotSetException;
import com.duoshouji.server.user.RegisteredUser;
import com.duoshouji.server.user.UserRepository;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

import junit.framework.Assert;

public class LoginFacadeImplTest {

	private static final VerificationCode MOCK_VERIFICATION_CODE = VerificationCode.valueOf("000000");
	private static final MobileNumber MOCK_MOBILE_NUMBER = new MobileNumber("13661863279");
	private static final Password MOCK_PASSWORD = Password.valueOf("******");
	
	private Mockery mockery;
	private UserRepository userRepository;
	private RegisteredUser user;
	private VerificationCodeLoginExecutor loginExecutor;
	private AccountSecurity accountSecurity;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		userRepository = mockery.mock(UserRepository.class);
		user = mockery.mock(RegisteredUser.class);
		loginExecutor = mockery.mock(VerificationCodeLoginExecutor.class);
		accountSecurity = mockery.mock(AccountSecurity.class);
	}
	
	@Test
	public void sendVerificationCode() {
		mockery.checking(new Expectations(){{
			oneOf(userRepository).findUser(MOCK_MOBILE_NUMBER); will(returnValue(user));
			oneOf(user).processVerificationCodeLogin(); will(returnValue(loginExecutor));
			oneOf(loginExecutor).sendVerificationCode();
		}});
		
		LoginFacadeImpl loginFacade  = new LoginFacadeImpl(userRepository);
		loginFacade.sendVerificationCode(MOCK_MOBILE_NUMBER);
	}
	
	@Test
	public void checkVerificationCode() {
		mockery.checking(new Expectations(){{
			oneOf(userRepository).findUser(MOCK_MOBILE_NUMBER); will(returnValue(user));
			oneOf(user).processVerificationCodeLogin(); will(returnValue(loginExecutor));
			oneOf(loginExecutor).authenticate(MOCK_VERIFICATION_CODE); will(returnValue(true));
		}});
		
		LoginFacadeImpl loginFacade  = new LoginFacadeImpl(userRepository);
		Assert.assertEquals(user, loginFacade.checkVerificationCode(MOCK_MOBILE_NUMBER, MOCK_VERIFICATION_CODE));
	}
	
	@Test
	public void verifyPassword() {
		mockery.checking(new Expectations(){{
			oneOf(userRepository).findUser(MOCK_MOBILE_NUMBER); will(returnValue(user));
			allowing(user).getAccountSecurity(); will(returnValue(accountSecurity));
			atLeast(1).of(accountSecurity).hasPassword(); will(returnValue(true));
			oneOf(accountSecurity).verifyPassword(MOCK_PASSWORD); will(returnValue(true));
		}});
		
		LoginFacadeImpl loginFacade  = new LoginFacadeImpl(userRepository);
		Assert.assertEquals(user, loginFacade.verifyPassword(MOCK_MOBILE_NUMBER, MOCK_PASSWORD));
	}
	
	@Test(expected = PasswordNotSetException.class)
	public void passwordNotSet() {
		mockery.checking(new Expectations(){{
			oneOf(userRepository).findUser(MOCK_MOBILE_NUMBER); will(returnValue(user));
			allowing(user).getAccountSecurity(); will(returnValue(accountSecurity));
			atLeast(1).of(accountSecurity).hasPassword(); will(returnValue(false));
		}});
		
		LoginFacadeImpl loginFacade  = new LoginFacadeImpl(userRepository);
		loginFacade.verifyPassword(MOCK_MOBILE_NUMBER, MOCK_PASSWORD);
	}
}
