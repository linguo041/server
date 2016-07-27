package com.duoshouji.server.internal.login;

import com.duoshouji.server.LoginFacade;
import com.duoshouji.server.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.user.UserRepository;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

public class LoginFacadeImpl implements LoginFacade {

	private UserRepository userRepository;
	
	public LoginFacadeImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public void sendVerificationCode(String accountId) {
		getVerificationCodeLoginExecutor(accountId).sendVerificationCode();
	}

	@Override
	public boolean checkVerificationCode(String accountId, VerificationCode verificationCode) {
		return getVerificationCodeLoginExecutor(accountId).verify(verificationCode);
	}

	@Override
	public boolean verifyPassword(String accountId, Password password) {
		return userRepository.findUser(accountId).getAccountSecurity().verifyPassword(password);
	}

	private VerificationCodeLoginExecutor getVerificationCodeLoginExecutor(String accountId) {
		return userRepository.findUser(accountId).processVerificationCodeLogin();
	}
}
