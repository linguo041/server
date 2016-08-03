package com.duoshouji.server;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MessageProxyFactory;
import com.duoshouji.server.util.UserMessageProxy;
import com.duoshouji.server.util.VerificationCode;

@Service
public class MockMessageSender implements MessageProxyFactory {
	
	private HashMap<UserIdentifier, VerificationCode> history = new HashMap<UserIdentifier, VerificationCode>();
	
	@Override
	public UserMessageProxy getMessageProxy(RegisteredUser user) {
		return new Proxy(user);
	}

	VerificationCode findHistory(UserIdentifier userId) {
		return history.get(userId);
	}
	
	private class Proxy implements UserMessageProxy {
		private RegisteredUser user;
		
		private Proxy(RegisteredUser user) {
			super();
			this.user = user;
		}

		@Override
		public void sendVerificationCode(VerificationCode verificationCode) {
			history.put(user.getIdentifier(), verificationCode);
		}
	}
}
