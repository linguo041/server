package com.duoshouji.server.service.user;

import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

public interface UserFacade {
	
	void sendLoginVerificationCode(MobileNumber accountId);
	
	RegisteredUser checkLoginVerificationCode(MobileNumber accountId, VerificationCode verificationCode);
	
	RegisteredUser checkLoginPassword(MobileNumber accountId, Password mockPassword);

	RegisteredUser findUser(UserIdentifier userId);

}
