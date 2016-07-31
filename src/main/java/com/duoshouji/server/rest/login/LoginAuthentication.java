package com.duoshouji.server.rest.login;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.duoshouji.server.Contants;
import com.duoshouji.server.service.login.LoginFacade;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.session.SessionManager;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@Path("/login/authenticate")
@Singleton
public class LoginAuthentication {
	
	private LoginFacade loginFacade;
	private SessionManager sessionManager;
	
	@Inject
	private LoginAuthentication(LoginFacade loginFacade, SessionManager sessionManager) {
		super();
		this.loginFacade = loginFacade;
		this.sessionManager = sessionManager;
	}

	@POST
	@Path("/verification-code")
	public Response authenticateVerificationCode(
		@FormParam("account") String mobileNumber,
		@FormParam("code") String verificationCode
			) {
		final MobileNumber mobile = new MobileNumber(mobileNumber);
		final RegisteredUser user = loginFacade.checkVerificationCode(mobile, VerificationCode.valueOf(verificationCode));
		return wrapUserInResponse(user);
	}
	
	@POST
	@Path("/credential")
	public Response authenticateCredential(
		@FormParam("account") String accountId,
		@FormParam("password") String password
			) {
		final MobileNumber mobile = new MobileNumber(accountId);
		final RegisteredUser user = loginFacade.verifyPassword(mobile, Password.valueOf(password));
		return wrapUserInResponse(user);
	}
	
	private Response wrapUserInResponse(RegisteredUser user) {
		final Response response;
		if (user != null) {
			final String token = sessionManager.newToken(user.getIdentifier());
			response = Response.ok().header(Contants.APP_TOKEN_HTTP_HEADER_NAME, token).build();
		} else {
			response = Response.ok().build();
		}
		return response;
	}
}
