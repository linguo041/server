package com.duoshouji.server;

import org.jvnet.hk2.annotations.Service;

import com.duoshouji.server.service.login.LoginFacade;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;
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

	public String findVerificationCode(String testAccountId) {
		return "xxxxxx";
	}

	@Override
	public void sendVerificationCode(MobileNumber accountId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RegisteredUser checkVerificationCode(MobileNumber accountId, VerificationCode verificationCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RegisteredUser verifyPassword(MobileNumber accountId, Password mockPassword) {
		// TODO Auto-generated method stub
		return null;
	}
}
