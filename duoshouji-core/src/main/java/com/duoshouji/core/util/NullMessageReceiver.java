package com.duoshouji.core.util;

import org.springframework.stereotype.Service;

import com.duoshouji.core.MessageProxy;
import com.duoshouji.core.MessageProxyFactory;
import com.duoshouji.service.util.MobileNumber;
import com.duoshouji.service.util.VerificationCode;

@Service
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
