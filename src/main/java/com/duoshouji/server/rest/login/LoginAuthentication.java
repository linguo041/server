package com.duoshouji.server.rest.login;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.duoshouji.server.Contants;
import com.duoshouji.server.LoginFacade;
import com.duoshouji.server.session.SessionManager;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@Path("/login/authenticate")
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
		@FormParam("account") String accountId,
		@FormParam("code") String verificationCode
			) {
		final Response response;
		final boolean verified = loginFacade.checkVerificationCode(accountId, VerificationCode.valueOf(verificationCode));
		if (verified) {
			final String token = sessionManager.newToken(accountId);
			response = Response.ok().header(Contants.APP_TOKEN_HTTP_HEADER_NAME, token).build();
		} else {
			response = Response.ok().build();
		}
		return response;
	}
	
	@POST
	@Path("/credential")
	public Response authenticateCredential(
		@FormParam("account") String accountId,
		@FormParam("password") String password
			) {
		final Response response;
		final boolean isPasswordMatch = loginFacade.verifyPassword(accountId, Password.valueOf(password));
		if (isPasswordMatch) {
			final String token = sessionManager.newToken(accountId);
			response = Response.ok().header(Contants.APP_TOKEN_HTTP_HEADER_NAME, token).build();
		} else {
			response = Response.ok().build();
		}
		return response;
	}
}
