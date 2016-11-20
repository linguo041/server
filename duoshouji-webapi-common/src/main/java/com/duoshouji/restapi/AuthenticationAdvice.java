package com.duoshouji.restapi;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.duoshouji.restapi.auth.UnauthenticatedUserException;
import com.duoshouji.restapi.auth.UserTokenService;

public abstract class AuthenticationAdvice {
	
	protected UserTokenService tokenService;
	
	@Required
	@Autowired
	public void setTokenService(UserTokenService tokenService) {
		this.tokenService = tokenService;
	}
	
	@ModelAttribute
	public void checkToken(Model model,
			HttpServletRequest request) throws UnauthenticatedUserException {
		final String token = request.getHeader(Constants.APP_TOKEN_HTTP_HEADER_NAME);
		if (token != null) {
			model.addAttribute("userId", Long.valueOf(tokenService.getUserId(token)));
		}
	}
}
