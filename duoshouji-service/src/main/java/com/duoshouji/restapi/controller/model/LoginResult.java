package com.duoshouji.restapi.controller.model;

public class LoginResult {

	private long userId;
	private String token;

	public LoginResult(long userId, String token) {
		this.userId = userId;
		this.token = token;
	}

	public String getToken() {
		return token;
	}
	
	public long getUserId() {
		return userId;
	}
}