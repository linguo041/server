package com.duoshouji.server.rest.login;


public class CredentialLoginResult extends LoginResult {

	private int loginResultCode;

	public CredentialLoginResult(int loginResultCode) {
		this(null, loginResultCode);
	}

	public CredentialLoginResult(String token, int loginResultCode) {
		super(token);
		this.loginResultCode = loginResultCode;
	}

	public int getLoginResultCode() {
		return loginResultCode;
	}

}
