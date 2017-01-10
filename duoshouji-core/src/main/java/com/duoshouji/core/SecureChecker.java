package com.duoshouji.core;

import com.duoshouji.service.util.VerificationCode;

public interface SecureChecker {
	
	void sendVerificationCode(MessageProxy messageProxy);

	boolean verify(VerificationCode verificationCode);
	
}
