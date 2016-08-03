package com.duoshouji.server.internal.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.login.LoginFacade;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserFacade;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@Service
public class LoginFacadeImpl implements LoginFacade {

	private UserFacade userFacade;
	
	@Autowired
	public LoginFacadeImpl(UserFacade userFacade) {
		this.userFacade = userFacade;
	}

	@Override
	public void sendVerificationCode(MobileNumber mobileNumber) {
		userFacade.getUser(mobileNumber).processVerificationCodeLogin().sendVerificationCode();
	}

	@Override
	public RegisteredUser checkVerificationCode(MobileNumber mobileNumber, VerificationCode verificationCode) {
		RegisteredUser loginUser = userFacade.getUser(mobileNumber);
		if (!loginUser.processVerificationCodeLogin().authenticate(verificationCode)) {
			loginUser = null;
		}
		return loginUser;
	}

	@Override
	public RegisteredUser verifyPassword(MobileNumber mobileNumber, Password password) {
		RegisteredUser loginUser = userFacade.getUser(mobileNumber);
		if (!loginUser.getAccountSecurity().verifyPassword(password)) {
			loginUser = null;
		}
		return loginUser;
	}
}
