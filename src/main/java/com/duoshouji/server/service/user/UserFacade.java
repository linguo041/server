package com.duoshouji.server.service.user;

import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

public interface UserFacade {
	
	void sendLoginVerificationCode(MobileNumber accountId);
	
	boolean checkLoginVerificationCode(MobileNumber accountId, VerificationCode verificationCode);
	
	boolean checkLoginPassword(MobileNumber accountId, Password mockPassword);

}
