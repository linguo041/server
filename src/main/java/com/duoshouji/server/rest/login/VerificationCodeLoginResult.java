package com.duoshouji.server.rest.login;


public class VerificationCodeLoginResult extends LoginResult {

	private boolean loginSuccess;

	public VerificationCodeLoginResult(boolean loginSuccess) {
		this(null, loginSuccess);
	}

	public VerificationCodeLoginResult(String token, boolean loginSuccess) {
		super(token);
		this.loginSuccess = loginSuccess;
	}

	public boolean isLoginSuccess() {
		return loginSuccess;
	}
}
