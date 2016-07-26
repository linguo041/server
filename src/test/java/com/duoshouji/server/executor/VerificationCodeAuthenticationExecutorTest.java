package com.duoshouji.server.executor;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.executor.VerificationCodeAuthenticationExecutor.State;
import com.duoshouji.server.util.UserMessageProxy;
import com.duoshouji.server.util.VerificationCodeGenerator;

public class VerificationCodeAuthenticationExecutorTest {

	private static final String MOCK_VERIFICATION_CODE = "000000";
	private static final String WRONG_VERIFICATION_CODE = "111111";
	
	private Mockery mockery;
	private UserMessageProxy mockSms;
	private VerificationCodeGenerator codeGenerator;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		mockSms = mockery.mock(UserMessageProxy.class);
		codeGenerator = mockery.mock(VerificationCodeGenerator.class);
	}
	
	@Test
	public void checkVerificationCode() {
		mockery.checking(new Expectations(){{
			oneOf(codeGenerator).generate(); will(returnValue(MOCK_VERIFICATION_CODE));
			oneOf(mockSms).sendVerificationCode(MOCK_VERIFICATION_CODE);
		}});
		
		VerificationCodeAuthenticationExecutor executor = new VerificationCodeAuthenticationExecutor(mockSms, codeGenerator);
		Assert.assertEquals(State.INIT, executor.getState());
		executor.sendVerificationCode();
		Assert.assertEquals(State.NOTIFIED, executor.getState());
		Assert.assertTrue(executor.verify(MOCK_VERIFICATION_CODE));
		Assert.assertEquals(State.VERIFIED, executor.getState());
	}
	
	@Test
	public void wrongVerificationCodeForTheFirstTimeAndThenCorrect() {
		mockery.checking(new Expectations(){{
			oneOf(codeGenerator).generate(); will(returnValue(MOCK_VERIFICATION_CODE));
			oneOf(mockSms).sendVerificationCode(MOCK_VERIFICATION_CODE);
		}});
		
		VerificationCodeAuthenticationExecutor executor = new VerificationCodeAuthenticationExecutor(mockSms, codeGenerator);
		Assert.assertEquals(State.INIT, executor.getState());
		executor.sendVerificationCode();
		Assert.assertEquals(State.NOTIFIED, executor.getState());
		Assert.assertFalse(executor.verify(WRONG_VERIFICATION_CODE));
		Assert.assertEquals(State.NOTIFIED, executor.getState());
		Assert.assertTrue(executor.verify(MOCK_VERIFICATION_CODE));
		Assert.assertEquals(State.VERIFIED, executor.getState());
	}
	
	@Test(expected=IllegalStateException.class)
	public void verifyBeforeSendVerificationCode() {
		VerificationCodeAuthenticationExecutor executor = new VerificationCodeAuthenticationExecutor(mockSms, codeGenerator);
		executor.verify(MOCK_VERIFICATION_CODE);
	}
	
	@Test(expected=IllegalStateException.class)
	public void redundantOperationAfterVerificationHasSuccess1() {
		mockery.checking(new Expectations(){{
			oneOf(codeGenerator).generate(); will(returnValue(MOCK_VERIFICATION_CODE));
			oneOf(mockSms).sendVerificationCode(MOCK_VERIFICATION_CODE);
		}});
		VerificationCodeAuthenticationExecutor executor = new VerificationCodeAuthenticationExecutor(mockSms, codeGenerator);
		executor.sendVerificationCode();
		Assert.assertTrue(executor.verify(MOCK_VERIFICATION_CODE));
		executor.sendVerificationCode();
	}
	
	@Test(expected=IllegalStateException.class)
	public void redundantOperationAfterVerificationHasSuccess2() {
		mockery.checking(new Expectations(){{
			oneOf(codeGenerator).generate(); will(returnValue(MOCK_VERIFICATION_CODE));
			oneOf(mockSms).sendVerificationCode(MOCK_VERIFICATION_CODE);
		}});
		VerificationCodeAuthenticationExecutor executor = new VerificationCodeAuthenticationExecutor(mockSms, codeGenerator);
		executor.sendVerificationCode();
		Assert.assertTrue(executor.verify(MOCK_VERIFICATION_CODE));
		executor.verify(MOCK_VERIFICATION_CODE);
	}
}
