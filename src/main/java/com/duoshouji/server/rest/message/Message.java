package com.duoshouji.server.rest.message;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.duoshouji.server.LoginFacade;

@Path("/message")
public class Message {

	private LoginFacade loginFacade;
	
	@Inject
	public Message(LoginFacade loginFacade) {
		super();
		this.loginFacade = loginFacade;
	}

	@POST
	@Path("/verification-code")
	public Response sendVerificationCode(
		@QueryParam("account") String accountId,
		@QueryParam("purpose") Purpose purpose 
			) {
		loginFacade.sendVerificationCode(accountId);
		return Response.ok().build();
	}
	
	public static enum Purpose {
		LOGIN,
		CHANGE_PASSWORD;
	}
}
