package com.duoshouji.server.service.login;

import org.jvnet.hk2.annotations.Contract;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@Contract
public interface LoginFacade {

	void sendVerificationCode(MobileNumber accountId);
	
	RegisteredUser checkVerificationCode(MobileNumber accountId, VerificationCode verificationCode);
	
	RegisteredUser verifyPassword(MobileNumber accountId, Password mockPassword);
}
