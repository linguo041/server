package com.duoshouji.restapi.controller.model.request;

import com.duoshouji.restapi.LoginMethod;

public class LoginRequestData {

	public LoginMethod loginMethod;
	public String mobileNumber;
	public String secret;

	public void setLoginMethod(LoginMethod loginMethod) {
		this.loginMethod = loginMethod;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}
