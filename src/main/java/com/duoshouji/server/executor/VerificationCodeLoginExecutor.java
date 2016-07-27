package com.duoshouji.server.executor;

import com.duoshouji.server.util.VerificationCode;

public interface VerificationCodeLoginExecutor {

	void sendVerificationCode();

	boolean verify(VerificationCode verificationCode);

}