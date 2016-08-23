package com.duoshouji.server.internal.executor;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.MockConstants;
import com.duoshouji.server.internal.executor.DelegatedVerificationCodeLoginExecutor;
import com.duoshouji.server.internal.executor.ExecutorHolder;
import com.duoshouji.server.service.verify.VerificationCodeAuthenticationExecutor;

public class DelegatedVerificationCodeLoginExecutorTest {

	private Mockery mockery;
	private ExecutorHolder holder;
	private VerificationCodeAuthenticationExecutor delegator;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		holder = mockery.mock(ExecutorHolder.class);
		delegator = mockery.mock(VerificationCodeAuthenticationExecutor.class);
	}
	
	@Test
	public void detachAfterVerifySuccess() {
		mockery.checking(new Expectations(){{
			oneOf(delegator).verify(MockConstants.MOCK_VERIFICATION_CODE); will(returnValue(true));
			oneOf(holder).detachExecutor(with(any(Object.class)));
		}});
		
		DelegatedVerificationCodeLoginExecutor executor = new DelegatedVerificationCodeLoginExecutor(delegator, holder);
		executor.authenticate(MockConstants.MOCK_VERIFICATION_CODE);
	}
	
	@Test
	public void doNotDetachBeforeVerifySuccess() {
		mockery.checking(new Expectations(){{
			oneOf(delegator).verify(MockConstants.MOCK_VERIFICATION_CODE); will(returnValue(false));
			never(holder).detachExecutor(with(any(Object.class)));
		}});
		
		DelegatedVerificationCodeLoginExecutor executor = new DelegatedVerificationCodeLoginExecutor(delegator, holder);
		executor.authenticate(MockConstants.MOCK_VERIFICATION_CODE);
	}

}
