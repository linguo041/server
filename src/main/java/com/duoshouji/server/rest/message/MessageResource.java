package com.duoshouji.server.rest.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.rest.Constants;
import com.duoshouji.server.rest.StandardJsonResponse;
import com.duoshouji.server.service.DuoShouJiFacade;
import com.duoshouji.server.util.MobileNumber;

@RequestMapping("/message/verification-code")
@RestController
public class MessageResource {

	private DuoShouJiFacade userFacade;
	
	@Autowired
	public MessageResource(DuoShouJiFacade userFacade) {
		super();
		this.userFacade = userFacade;
	}

	@RequestMapping(path = "/login", method = RequestMethod.POST)
	public StandardJsonResponse sendLoginVerificationCode(
		@RequestParam("mobile") String mobileNumber
			) {
		userFacade.sendLoginVerificationCode(new MobileNumber(mobileNumber));
		return StandardJsonResponse.emptyResponse();
	}
	
	@RequestMapping(path = "/reset-password", method = RequestMethod.POST)
	public StandardJsonResponse sendResetPasswordVerificationCode(
			@RequestHeader(name=Constants.APP_TOKEN_HTTP_HEADER_NAME) String token
		) {
		userFacade.sendResetPasswordVerificationCode(token);
		return StandardJsonResponse.emptyResponse();
	}
	
}
