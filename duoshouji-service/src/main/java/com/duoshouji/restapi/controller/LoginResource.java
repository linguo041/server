package com.duoshouji.restapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.duoshouji.restapi.auth.UserTokenService;
import com.duoshouji.restapi.controller.model.LoginResult;
import com.duoshouji.restapi.controller.model.WrongPasswordException;
import com.duoshouji.restapi.controller.model.WrongVerificationCodeException;
import com.duoshouji.service.user.Password;
import com.duoshouji.service.user.UserFacade;
import com.duoshouji.service.util.MobileNumber;
import com.duoshouji.service.util.VerificationCode;

@Controller
public class LoginResource {
		
	private UserFacade userFacade;
	private UserTokenService tokenService;

	@Autowired
	public LoginResource(UserFacade userFacade, UserTokenService tokenService) {
		this.userFacade = userFacade;
		this.tokenService = tokenService;
	}

	@PostMapping(path="/user/login", params="code")
	@ResponseBody
	public LoginResult verificationCodeLogin(
		@RequestParam("mobile") MobileNumber mobileNumber,
		@RequestParam("code") VerificationCode verificationCode
			) {
		final long userId = userFacade.verificationCodeLogin(mobileNumber, verificationCode);
		if (userId == UserFacade.NULL_USER_ID) {
			throw new WrongVerificationCodeException();
		}
		return new LoginResult(tokenService.newToken(userId));
	}
	
	@PostMapping(path = "/user/login", params = "password")
	@ResponseBody
	public LoginResult credentialLogin(
		@RequestParam("mobile") MobileNumber mobileNumber,
		@RequestParam("password") String password
			) {
		final long userId = userFacade.passwordLogin(mobileNumber, Password.valueOf(password));
		if (userId == UserFacade.NULL_USER_ID) {
			throw new WrongPasswordException();
		}		
		return new LoginResult(tokenService.newToken(userId));
	}
	
	@PostMapping(path="/message/verification-code", params="purpose=login")
	@ResponseBody
	public void sendLoginVerificationCode(
			@RequestParam("mobile") MobileNumber mobileNumber) {
		userFacade.sendLoginVerificationCode(mobileNumber);
	}
}
