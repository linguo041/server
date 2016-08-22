package com.duoshouji.server.service.verify;

import com.duoshouji.server.util.VerificationCode;

public interface SecureChecker {
	
	void sendVerificationCode();

	boolean verify(VerificationCode verificationCode);
	
}
