package com.duoshouji.server.rest.login;


public abstract class LoginResult {

	private String token;

	public LoginResult(String token) {
		super();
		this.token = token;
	}

	public String getToken() {
		return token;
	}
	
	void setToken(String token) {
		this.token = token;
	}
}
