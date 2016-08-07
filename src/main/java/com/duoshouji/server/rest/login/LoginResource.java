package com.duoshouji.server.rest.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.rest.Constants;
import com.duoshouji.server.service.login.LoginFacade;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.session.TokenManager;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@RequestMapping("/login/authenticate")
@RestController
public class LoginResource {
	
	private LoginFacade loginFacade;
	private TokenManager sessionManager;
	
	@Autowired
	private LoginResource(LoginFacade loginFacade, TokenManager sessionManager) {
		super();
		this.loginFacade = loginFacade;
		this.sessionManager = sessionManager;
	}

	@RequestMapping(path = "/verification-code", method = RequestMethod.POST)
	public ResponseEntity<?> authenticateVerificationCode(
		@RequestParam("mobile") String mobileNumber,
		@RequestParam("code") String verificationCode
			) {
		final MobileNumber mobile = new MobileNumber(mobileNumber);
		final RegisteredUser user = loginFacade.checkVerificationCode(mobile, VerificationCode.valueOf(verificationCode));
		return wrapUserInResponse(user);
	}
	
	@RequestMapping(path = "/credential", method = RequestMethod.POST)
	public ResponseEntity<?> authenticateCredential(
		@RequestParam("mobile") String mobileNumber,
		@RequestParam("password") String password
			) {
		final MobileNumber mobile = new MobileNumber(mobileNumber);
		final RegisteredUser user = loginFacade.verifyPassword(mobile, Password.valueOf(password));
		return wrapUserInResponse(user);
	}
	
	private ResponseEntity<?> wrapUserInResponse(RegisteredUser user) {
		HttpHeaders headers = new HttpHeaders();
		if (user != null) {
			headers.add(Constants.APP_TOKEN_HTTP_HEADER_NAME, sessionManager.newToken(user.getIdentifier()));
		}
		return new ResponseEntity<>(null, headers, HttpStatus.OK);
	}
}
