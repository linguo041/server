package com.duoshouji.server.rest.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.service.login.LoginFacade;
import com.duoshouji.server.util.MobileNumber;

@RequestMapping("/message")
@RestController
public class MessageResource {

	private LoginFacade loginFacade;
	
	@Autowired
	public MessageResource(LoginFacade loginFacade) {
		super();
		this.loginFacade = loginFacade;
	}

	@RequestMapping(path = "/verification-code", method = RequestMethod.POST)
	public ResponseEntity<?> sendVerificationCode(
		@RequestParam("mobile") String mobileNumber,
		@RequestParam("purpose") Purpose purpose 
			) {
		loginFacade.sendVerificationCode(new MobileNumber(mobileNumber));
		return new ResponseEntity<>(null, null, HttpStatus.OK);
	}
	
	public static enum Purpose {
		LOGIN,
		CHANGE_PASSWORD;
	}
}
