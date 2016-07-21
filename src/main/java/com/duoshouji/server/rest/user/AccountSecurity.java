package com.duoshouji.server.rest.user;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/accounts/{account-id}/security")
public class AccountSecurity {

	@POST
	@Path("/password")
	public void changePassword(
		@QueryParam("account") String accountId,
		@QueryParam("password") String password
			) {
	}
	
	@POST
	@Path("/logout")
	public void logout(
		@QueryParam("account") String accountId
			) {
	}
}
