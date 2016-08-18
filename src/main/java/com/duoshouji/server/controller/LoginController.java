package com.duoshouji.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.duoshouji.server.service.login.LoginFacade;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;

@Controller
@RequestMapping("/auth")
public class LoginController {
	
//	@Autowired
	private LoginFacade loginFacade;

	@RequestMapping(value="/verifyPassword", method=RequestMethod.POST)
	@ResponseBody
	public RegisteredUser verifyPassword(MobileNumber mobileNumber, Password password) {
		return loginFacade.verifyPassword(mobileNumber, password);
	}
}
