package com.duoshouji.server.rest.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.rest.StandardJsonResponse;
import com.duoshouji.server.service.user.UserFacade;
import com.duoshouji.server.util.MobileNumber;

@RequestMapping("/message")
@RestController
public class MessageResource {

	private UserFacade userFacade;
	
	@Autowired
	public MessageResource(UserFacade userFacade) {
		super();
		this.userFacade = userFacade;
	}

	@RequestMapping(path = "/verification-code/login", method = RequestMethod.POST)
	public StandardJsonResponse sendLoginVerificationCode(
		@RequestParam("mobile") String mobileNumber
			) {
		userFacade.sendLoginVerificationCode(new MobileNumber(mobileNumber));
		return StandardJsonResponse.emptyResponse();
	}
}
