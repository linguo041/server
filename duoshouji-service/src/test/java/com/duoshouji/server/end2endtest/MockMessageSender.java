package com.duoshouji.server.end2endtest;

import java.util.HashMap;

import com.duoshouji.core.MessageProxy;
import com.duoshouji.core.MessageProxyFactory;
import com.duoshouji.service.util.MobileNumber;
import com.duoshouji.service.util.VerificationCode;

public class MockMessageSender implements MessageProxyFactory {
	
	private HashMap<MobileNumber, Proxy> proxies = new HashMap<MobileNumber, Proxy>();
	
	@Override
	public MessageProxy getMessageProxy(MobileNumber mobileNumber) {
		Proxy proxy = proxies.get(mobileNumber);
		if (proxy == null) {
			proxy = new Proxy();
			proxies.put(mobileNumber, proxy);
		}
		return proxy;
	}

	VerificationCode findHistory(MobileNumber mobileNumber) {
		return proxies.get(mobileNumber).lastCode;
	}
	
	private class Proxy implements MessageProxy {
		private VerificationCode lastCode;

		@Override
		public void sendVerificationCode(VerificationCode verificationCode) {
			lastCode = verificationCode;
		}

		@Override
		public void sendInvitationMessage(MobileNumber inviterMobileNumber) {
		}
	}
}
