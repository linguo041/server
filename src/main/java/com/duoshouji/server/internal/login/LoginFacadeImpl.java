package com.duoshouji.server.internal.login;

import com.duoshouji.server.login.LoginFacade;
import com.duoshouji.server.user.PasswordNotSetException;
import com.duoshouji.server.user.RegisteredUser;
import com.duoshouji.server.user.UserRepository;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

public class LoginFacadeImpl implements LoginFacade {

	private UserRepository userRepository;
	
	public LoginFacadeImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public void sendVerificationCode(MobileNumber mobile) {
		userRepository.findUser(mobile).processVerificationCodeLogin().sendVerificationCode();
	}

	@Override
	public RegisteredUser checkVerificationCode(MobileNumber accountId, VerificationCode verificationCode) {
		RegisteredUser loginUser = userRepository.findUser(accountId);
		if (!loginUser.processVerificationCodeLogin().authenticate(verificationCode)) {
			loginUser = null;
		}
		return loginUser;
	}

	@Override
	public RegisteredUser verifyPassword(MobileNumber mobile, Password password) {
		RegisteredUser loginUser = userRepository.findUser(mobile);
		if (!loginUser.getAccountSecurity().hasPassword()) {
			throw new PasswordNotSetException("Password not set, can't apply credential login!");
		}
		if (!loginUser.getAccountSecurity().verifyPassword(password)) {
			loginUser = null;
		}
		return loginUser;
	}
}
