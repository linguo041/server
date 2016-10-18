package com.duoshouji.restapi.controller.model;

public class LoginResult {

	private String token;

	public LoginResult(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}
}