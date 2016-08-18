package com.duoshouji.server.rest.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.rest.StandardJsonResponse;
import com.duoshouji.server.service.user.PasswordNotSetException;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserFacade;
import com.duoshouji.server.session.TokenManager;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@RequestMapping("/login/authenticate")
@RestController
public class LoginResource {
	
	private UserFacade userFacade;
	private TokenManager tokenManager;
	
	@Autowired
	public LoginResource(UserFacade userFacade, TokenManager tokenManager) {
		super();
		this.userFacade = userFacade;
		this.tokenManager = tokenManager;
	}

	@RequestMapping(path = "/verification-code", method = RequestMethod.POST)
	public StandardJsonResponse authenticateVerificationCode(
		@RequestParam("mobile") String mobileNumber,
		@RequestParam("code") String verificationCode
			) {
		final MobileNumber mobile = new MobileNumber(mobileNumber);
		final RegisteredUser loginUser = userFacade.checkLoginVerificationCode(mobile, VerificationCode.valueOf(verificationCode));
		VerificationCodeLoginResult result;
		if (loginUser != null) {
			result = new VerificationCodeLoginResult(tokenManager.newToken(loginUser.getIdentifier()), true);
		} else {
			result = new VerificationCodeLoginResult(false);
		}
		return StandardJsonResponse.wrapResponse(result);
	}
	
	@RequestMapping(path = "/credential", method = RequestMethod.POST)
	public StandardJsonResponse authenticateCredential(
		@RequestParam("mobile") String mobileNumber,
		@RequestParam("password") String password
			) {
		final MobileNumber mobile = new MobileNumber(mobileNumber);
		CredentialLoginResult result;
		final RegisteredUser loginUser = userFacade.checkLoginPassword(mobile, Password.valueOf(password));
		try {
			if (loginUser != null) {
				result = new CredentialLoginResult(tokenManager.newToken(loginUser.getIdentifier()), 0);
			} else {
				result = new CredentialLoginResult(2);
			}
		} catch (PasswordNotSetException ex) {
			result = new CredentialLoginResult(1);
		}
		return StandardJsonResponse.wrapResponse(result);
	}
}
