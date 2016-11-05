package com.duoshouji.restapi.controller.model.request;

import com.duoshouji.restapi.LoginMethod;
import com.duoshouji.service.util.MobileNumber;

public class LoginRequestData {

	private String loginMethod;
	private String mobileNumber;
	private String secret;

	public void setLoginMethod(String loginMethod) {
		this.loginMethod = loginMethod;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public LoginMethod getLoginMethod() {
		return LoginMethod.valueOf(loginMethod);
	}
	
	public MobileNumber getMobileNumber() {
		return MobileNumber.valueOf(mobileNumber);
	}
	
	public String getSecret() {
		return secret;
	}
}
