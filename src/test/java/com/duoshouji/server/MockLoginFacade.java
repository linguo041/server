package com.duoshouji.server;

import org.jvnet.hk2.annotations.Service;

import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@Service
public class MockLoginFacade implements LoginFacade {

	private static MockLoginFacade instance;
	
	private MockLoginFacade() {
	}
	
	public static MockLoginFacade getInstance() {
		if (instance == null) {
			instance = new MockLoginFacade();
		}
		return instance;
	}
	
	@Override
	public void sendVerificationCode(String accountId) {

	}

	@Override
	public boolean checkVerificationCode(String accountId, VerificationCode verificationCode) {
		return true;
	}

	@Override
	public boolean verifyPassword(String accountId, Password password) {
		return true;
	}

	public String findVerificationCode(String testAccountId) {
		return "xxxxxx";
	}
}
