package com.duoshouji.server.internal.login;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.user.AccountSecurity;
import com.duoshouji.server.user.RegisteredUser;
import com.duoshouji.server.user.UserRepository;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

import junit.framework.Assert;

public class LoginFacadeImplTest {

	private static final VerificationCode MOCK_VERIFICATION_CODE = VerificationCode.valueOf("000000");
	private static final String MOCK_ACCOUNT_ID = "13661863279";
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
			oneOf(userRepository).findUser(MOCK_ACCOUNT_ID); will(returnValue(user));
			oneOf(user).processVerificationCodeLogin(); will(returnValue(loginExecutor));
			oneOf(loginExecutor).sendVerificationCode();
		}});
		
		LoginFacadeImpl loginFacade  = new LoginFacadeImpl(userRepository);
		loginFacade.sendVerificationCode(MOCK_ACCOUNT_ID);
	}
	
	@Test
	public void checkVerificationCode() {
		mockery.checking(new Expectations(){{
			oneOf(userRepository).findUser(MOCK_ACCOUNT_ID); will(returnValue(user));
			oneOf(user).processVerificationCodeLogin(); will(returnValue(loginExecutor));
			oneOf(loginExecutor).verify(MOCK_VERIFICATION_CODE); will(returnValue(true));
		}});
		
		LoginFacadeImpl loginFacade  = new LoginFacadeImpl(userRepository);
		Assert.assertTrue(loginFacade.checkVerificationCode(MOCK_ACCOUNT_ID, MOCK_VERIFICATION_CODE));
	}
	
	@Test
	public void verifyPassword() {
		mockery.checking(new Expectations(){{
			oneOf(userRepository).findUser(MOCK_ACCOUNT_ID); will(returnValue(user));
			oneOf(user).getAccountSecurity(); will(returnValue(accountSecurity));
			oneOf(accountSecurity).verifyPassword(MOCK_PASSWORD); will(returnValue(true));
		}});
		
		LoginFacadeImpl loginFacade  = new LoginFacadeImpl(userRepository);
		Assert.assertTrue(loginFacade.verifyPassword(MOCK_ACCOUNT_ID, MOCK_PASSWORD));
	}
}
