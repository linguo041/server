package com.duoshouji.server;

import com.duoshouji.server.util.VerificationCode;

public interface LoginFacade {

	void sendVerificationCode(String accountId);
	
	boolean checkVerificationCode(String accountId, VerificationCode verificationCode);
}
