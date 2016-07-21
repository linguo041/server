package com.duoshouji.server.rest.message;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/message")
public class Message {

	@POST
	@Path("/verification-code")
	public void sendVerificationCode(
		@QueryParam("account") String accountId,
		@QueryParam("purpose") Purpose purpose 
			) {
	}
	
	public static enum Purpose {
		LOGIN,
		CHANGE_PASSWORD;
	}
}
