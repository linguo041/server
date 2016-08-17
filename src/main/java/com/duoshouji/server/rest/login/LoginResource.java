package com.duoshouji.server.rest.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.rest.StandardJsonResponse;
import com.duoshouji.server.service.user.PasswordNotSetException;
import com.duoshouji.server.service.user.UserFacade;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@RequestMapping("/login/authenticate")
@RestController
public class LoginResource {
	
	private UserFacade userFacade;
	
	@Autowired
	private LoginResource(UserFacade loginFacade) {
		super();
		this.userFacade = loginFacade;
	}

	@RequestMapping(path = "/verification-code", method = RequestMethod.POST)
	public StandardJsonResponse authenticateVerificationCode(
		@RequestParam("mobile") String mobileNumber,
		@RequestParam("code") String verificationCode
			) {
		final MobileNumber mobile = new MobileNumber(mobileNumber);
		final boolean success = userFacade.checkLoginVerificationCode(mobile, VerificationCode.valueOf(verificationCode));
		return StandardJsonResponse.wrapResponse(new Object(){
			@SuppressWarnings("unused")
			public boolean getLoginSuccess() {
				return success;
			}
		});
	}
	
	@RequestMapping(path = "/credential", method = RequestMethod.POST)
	public StandardJsonResponse authenticateCredential(
		@RequestParam("mobile") String mobileNumber,
		@RequestParam("password") String password
			) {
		final MobileNumber mobile = new MobileNumber(mobileNumber);
		int result = 0;
		try {
			if (!userFacade.checkLoginPassword(mobile, Password.valueOf(password))) {
				result = 2;
			}
		} catch (PasswordNotSetException ex) {
			result = 1;
		}
		final int finalResult = result;
		return StandardJsonResponse.wrapResponse(new Object(){
			@SuppressWarnings("unused")
			public int getLoginResultCode() {
				return finalResult;
			}
		});
	}
}
