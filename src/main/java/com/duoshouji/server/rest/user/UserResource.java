package com.duoshouji.server.rest.user;

import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.service.user.UserFacade;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.Password;

@RequestMapping("/accounts/${account-id}")
@RestController
public class UserResource {
	
	private UserFacade userFacade;

	@RequestMapping(path = "/settings/security/password", method = RequestMethod.POST)
	public void setPassword(
			@RequestAttribute("user") UserIdentifier userId,
			@RequestParam("password") String password) {
		userFacade.changePassword(userId, Password.valueOf(password));
	}
}
