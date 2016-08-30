package com.duoshouji.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.service.DuoShouJiFacade;
import com.duoshouji.server.service.auth.UserTokenService;
import com.duoshouji.server.service.user.PasswordNotSetException;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@RestController
public class LoginResource {

	private DuoShouJiFacade douShouJiFacade;
	private UserTokenService tokenService;
	
	@Autowired
	private LoginResource(DuoShouJiFacade douShouJiFacade, UserTokenService tokenService) {
		super();
		this.douShouJiFacade = douShouJiFacade;
		this.tokenService = tokenService;
	}

	@RequestMapping(path = "/accounts/{account-id}/message/verification-code/login", method = RequestMethod.POST)
	public void sendLoginVerificationCode(
		@PathVariable("account-id") MobileNumber mobileNumber
			) {
		douShouJiFacade.sendLoginVerificationCode(mobileNumber);
	}
	
	@RequestMapping(path = "/accounts/{account-id}/login/verification-code", method = RequestMethod.POST)
	public VerificationCodeLoginResult authenticateVerificationCode(
		@PathVariable("account-id") MobileNumber mobileNumber,
		@RequestParam("code") String verificationCode
			) {
		final boolean verified = douShouJiFacade.verificationCodeLogin(mobileNumber, VerificationCode.valueOf(verificationCode));
		VerificationCodeLoginResult result;
		if (verified) {
			result = new VerificationCodeLoginResult(tokenService.newToken(mobileNumber), true);
		} else {
			result = new VerificationCodeLoginResult(false);
		}
		return result;
	}
	
	@RequestMapping(path = "/accounts/{account-id}/login/credential", method = RequestMethod.POST)
	public CredentialLoginResult authenticateCredential(
		@RequestParam("mobile") MobileNumber mobileNumber,
		@RequestParam("password") String password
			) {
		final boolean verified = douShouJiFacade.passwordLogin(mobileNumber, Password.valueOf(password));
		CredentialLoginResult result;
		try {
			if (verified) {
				result = new CredentialLoginResult(tokenService.newToken(mobileNumber), 0);
			} else {
				result = new CredentialLoginResult(2);
			}
		} catch (PasswordNotSetException ex) {
			result = new CredentialLoginResult(1);
		}
		return result;
	}
	
	private static abstract class LoginResult {

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
	
	public static class VerificationCodeLoginResult extends LoginResult {

		private boolean loginSuccess;

		public VerificationCodeLoginResult(boolean loginSuccess) {
			this(null, loginSuccess);
		}

		public VerificationCodeLoginResult(String token, boolean loginSuccess) {
			super(token);
			this.loginSuccess = loginSuccess;
		}

		public boolean isLoginSuccess() {
			return loginSuccess;
		}
	}
	
	public static class CredentialLoginResult extends LoginResult {

		private int loginResultCode;

		public CredentialLoginResult(int loginResultCode) {
			this(null, loginResultCode);
		}

		public CredentialLoginResult(String token, int loginResultCode) {
			super(token);
			this.loginResultCode = loginResultCode;
		}

		public int getLoginResultCode() {
			return loginResultCode;
		}

	}

}
