package com.duoshouji.restapi.controller.model.response;

public class LoginResponseData {

	private String token;

	public LoginResponseData(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}
}