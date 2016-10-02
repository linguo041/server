package com.duoshouji.server.internal.core;

import com.duoshouji.server.util.MessageProxy;
import com.duoshouji.server.util.MessageProxyFactory;
import com.duoshouji.server.util.VerificationCode;
import com.duoshouji.util.MobileNumber;

public class NullMessageReceiver implements MessageProxyFactory {
	private static MessageProxy NULL_MESSAGE_RECEIVER = new MessageProxy() {

		@Override
		public void sendVerificationCode(VerificationCode verificationCode) {			
		}

		@Override
		public void sendInvitationMessage(MobileNumber inviterMobileNumber) {
		}
		
	};
	@Override
	public MessageProxy getMessageProxy(MobileNumber mobileNumber) {
		return NULL_MESSAGE_RECEIVER;
	}

}
