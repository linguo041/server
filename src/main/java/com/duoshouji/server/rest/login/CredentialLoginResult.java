package com.duoshouji.server.rest.login;

import com.duoshouji.server.service.user.UserIdentifier;

public class CredentialLoginResult extends LoginResult {

	private int loginResultCode;

	public CredentialLoginResult(UserIdentifier userId, int loginResultCode) {
		super(userId);
		this.loginResultCode = loginResultCode;
	}

	public int getLoginResultCode() {
		return loginResultCode;
	}

}
