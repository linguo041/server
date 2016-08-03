package com.duoshouji.server.service.executor;

import com.duoshouji.server.util.VerificationCode;

public interface VerificationCodeLoginExecutor {

	void sendVerificationCode();

	boolean authenticate(VerificationCode verificationCode);

}