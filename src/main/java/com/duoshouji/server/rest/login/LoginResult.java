package com.duoshouji.server.rest.login;

import com.duoshouji.server.service.user.UserIdentifier;

public abstract class LoginResult {

	private UserIdentifier userId;

	public LoginResult(UserIdentifier userId) {
		super();
		this.userId = userId;
	}

	public UserIdentifier getUserId() {
		return userId;
	}
}
