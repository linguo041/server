package com.duoshouji.server.end2endtest;

import java.util.HashMap;

import com.duoshouji.server.service.user.FullFunctionalUser;
import com.duoshouji.server.util.MessageProxyFactory;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.UserMessageProxy;
import com.duoshouji.server.util.VerificationCode;

public class MockMessageSender implements MessageProxyFactory {
	
	private HashMap<MobileNumber, Proxy> proxies = new HashMap<MobileNumber, Proxy>();
	
	@Override
	public UserMessageProxy getMessageProxy(FullFunctionalUser user) {
		final MobileNumber mobile = user.getMobileNumber();
		Proxy proxy = proxies.get(mobile);
		if (proxy == null) {
			proxy = new Proxy();
			proxies.put(mobile, proxy);
		}
		return proxy;
	}

	VerificationCode findHistory(MobileNumber mobileNumber) {
		return proxies.get(mobileNumber).lastCode;
	}
	
	private class Proxy implements UserMessageProxy {
		private VerificationCode lastCode;

		@Override
		public void sendVerificationCode(VerificationCode verificationCode) {
			lastCode = verificationCode;
		}
	}
}
