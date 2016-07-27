package com.duoshouji.server;

import org.jvnet.hk2.annotations.Contract;

import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@Contract
public interface LoginFacade {

	void sendVerificationCode(String accountId);
	
	boolean checkVerificationCode(String accountId, VerificationCode verificationCode);
	
	boolean verifyPassword(String accountId, Password mockPassword);
}
