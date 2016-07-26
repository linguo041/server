package com.duoshouji.server.rest.login;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.duoshouji.server.Contants;
import com.duoshouji.server.LoginFacade;
import com.duoshouji.server.user.UserFacade;
import com.duoshouji.server.util.VerificationCode;

@Path("/login/authenticate")
public class LoginAuthentication {
	
	private LoginFacade loginFacade;
	private UserFacade userFacade;
	
	@Inject
	private LoginAuthentication(LoginFacade loginFacade, UserFacade userFacade) {
		super();
		this.loginFacade = loginFacade;
		this.userFacade = userFacade;
	}

	@POST
	@Path("/verification-code")
	public Response authenticateVerificationCode(
		@QueryParam("account") String accountId,
		@QueryParam("code") String verificationCode
			) {
		final Response response;
		final boolean verified = loginFacade.checkVerificationCode(accountId, VerificationCode.valueOf(verificationCode));
		if (verified) {
			response = Response.ok().header(Contants.APP_TOKEN_HTTP_HEADER_NAME, "token").build();
		} else {
			response = Response.status(202).build();
		}
		return response;
	}
	
	@POST
	@Path("/credential")
	public Response authenticateCredential(
		@QueryParam("account") String accountId,
		@QueryParam("password") String password
			) {
		final Response response;
		final boolean isPasswordMatch = userFacade.verifyPassword(accountId, password);
		if (isPasswordMatch) {
			response = Response.ok().header(Contants.APP_TOKEN_HTTP_HEADER_NAME, "token").build();
		} else {
			response = Response.status(202).build();
		}
		return response;
	}
}
