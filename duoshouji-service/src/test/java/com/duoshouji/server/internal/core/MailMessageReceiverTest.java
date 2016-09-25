package com.duoshouji.server.internal.core;

import org.junit.Test;

import com.duoshouji.server.MockConstants;

public class MailMessageReceiverTest {

	@Test
	public void testJavaMail() {
		EmailMessageReceiver receiver = new EmailMessageReceiver();
		receiver.getMessageProxy(MockConstants.MOCK_MOBILE_NUMBER).sendVerificationCode(MockConstants.MOCK_VERIFICATION_CODE);
	}
}
