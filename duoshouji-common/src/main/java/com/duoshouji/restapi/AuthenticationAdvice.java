package com.duoshouji.restapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;

import com.duoshouji.restapi.auth.UnauthenticatedUserException;
import com.duoshouji.restapi.auth.UserTokenService;
import com.duoshouji.service.user.UserFacade;

public abstract class AuthenticationAdvice {
	
	protected UserTokenService tokenService;
	
	@Required
	@Autowired
	public void setTokenService(UserTokenService tokenService) {
		this.tokenService = tokenService;
	}
	
	@ModelAttribute
	public void checkToken(Model model,
			@RequestHeader(name=Constants.APP_TOKEN_HTTP_HEADER_NAME) String token) throws UnauthenticatedUserException {
		final long userId = tokenService.getUserId(token);
		if (userId == UserFacade.NULL_USER_ID) {
			throw new UnauthenticatedUserException("Token is expired or not exists. Please login first.");
		}
		model.addAttribute("userId", Long.valueOf(userId));
	}
}
