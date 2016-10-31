package com.duoshouji.restapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.duoshouji.restapi.LoginMethod;
import com.duoshouji.restapi.UnrecognizableLoginMethodException;
import com.duoshouji.restapi.auth.UserTokenService;
import com.duoshouji.restapi.controller.model.request.LoginRequestData;
import com.duoshouji.restapi.controller.model.request.SendVerificationCodeRequestData;
import com.duoshouji.restapi.controller.model.response.LoginResponseData;
import com.duoshouji.restapi.controller.model.response.WrongPasswordException;
import com.duoshouji.restapi.controller.model.response.WrongVerificationCodeException;
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

	@PostMapping("/user/login")
	@ResponseBody
	public LoginResponseData userLogin(
			@RequestBody LoginRequestData requestData) {
		final MobileNumber mobileNumber = MobileNumber.valueOf(requestData.mobileNumber);
		long userId;
		if (requestData.loginMethod == LoginMethod.CREDENTIAL) {
			userId = userFacade.passwordLogin(mobileNumber, Password.valueOf(requestData.secret));
			if (userId == UserFacade.NULL_USER_ID) {
				throw new WrongPasswordException();
			}		
		} else if (requestData.loginMethod == LoginMethod.VERIFICATION_CODE) {
			userId = userFacade.verificationCodeLogin(mobileNumber, VerificationCode.valueOf(requestData.secret));
			if (userId == UserFacade.NULL_USER_ID) {
				throw new WrongVerificationCodeException();
			}
		} else {
			throw new UnrecognizableLoginMethodException(requestData.loginMethod);
		}
		return new LoginResponseData(tokenService.newToken(userId));
	}
	
	@PostMapping("/message/verification-code")
	@ResponseBody
	public void sendLoginVerificationCode(
			@RequestBody SendVerificationCodeRequestData requestData) {
		userFacade.sendLoginVerificationCode(mobileNumber);
	}
}
