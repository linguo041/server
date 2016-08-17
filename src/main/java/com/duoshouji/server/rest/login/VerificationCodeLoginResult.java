package com.duoshouji.server.rest.login;

import com.duoshouji.server.service.user.UserIdentifier;

public class VerificationCodeLoginResult extends LoginResult {

	private boolean loginSuccess;

	public VerificationCodeLoginResult(UserIdentifier userId,
			boolean loginSuccess) {
		super(userId);
		this.loginSuccess = loginSuccess;
	}

	public boolean isLoginSuccess() {
		return loginSuccess;
	}
}
