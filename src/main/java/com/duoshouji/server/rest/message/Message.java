package com.duoshouji.server.rest.message;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.duoshouji.server.service.login.LoginFacade;
import com.duoshouji.server.util.MobileNumber;

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
		@FormParam("account") String mobile,
		@FormParam("purpose") Purpose purpose 
			) {
		loginFacade.sendVerificationCode(new MobileNumber(mobile));
		return Response.ok().build();
	}
	
	public static enum Purpose {
		LOGIN,
		CHANGE_PASSWORD;
	}
}
