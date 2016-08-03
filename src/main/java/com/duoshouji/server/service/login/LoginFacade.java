package com.duoshouji.server.service.login;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

public interface LoginFacade {

	void sendVerificationCode(MobileNumber accountId);
	
	RegisteredUser checkVerificationCode(MobileNumber accountId, VerificationCode verificationCode);
	
	RegisteredUser verifyPassword(MobileNumber accountId, Password mockPassword);
}
