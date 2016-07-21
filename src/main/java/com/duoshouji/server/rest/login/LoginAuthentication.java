package com.duoshouji.server.rest.login;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/login/authenticate")
public class LoginAuthentication {

	@POST
	@Path("/verification-code")
	public void authenticateVerificationCode(
		@QueryParam("account") String accountId,
		@QueryParam("code") String verificationCode
			) {
		
	}
	
	@POST
	@Path("/credential")
	public void authenticateCredential(
		@QueryParam("account") String accountId,
		@QueryParam("password") String password
			) {
		
	}
}
