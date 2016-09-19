package com.duoshouji.server.internal.core;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.duoshouji.server.MockConstants;
import com.duoshouji.server.service.user.FullFunctionalUser;

public class MailMessageReceiverTest {

	private Mockery mockery;
	private FullFunctionalUser mockUser;
	
	@Before
	public void setup() {
		mockery = new Mockery();
		mockUser = mockery.mock(FullFunctionalUser.class);
	}
	
	@Test
	public void testJavaMail() {
		mockery.checking(new Expectations() {{
			allowing(mockUser).getMobileNumber(); will(returnValue(MockConstants.MOCK_MOBILE_NUMBER));
		}});
		
		EmailMessageReceiver receiver = new EmailMessageReceiver();
		receiver.getMessageProxy(mockUser).sendVerificationCode(MockConstants.MOCK_VERIFICATION_CODE);
	}
}
