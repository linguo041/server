package com.duoshouji.server.rest.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.duoshouji.server.rest.Constants;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserFacade;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.session.TokenManager;

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

	private TokenManager tokenManager;
	private UserFacade userFacade;

	@Autowired
	public AuthenticationInterceptor(TokenManager tokenManager, UserFacade userFacade) {
		super();
		this.tokenManager = tokenManager;
		this.userFacade = userFacade;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		final boolean returnValue = super.preHandle(request, response, handler);
		final String token = request.getHeader(Constants.APP_TOKEN_HTTP_HEADER_NAME);
		if (token != null) {
			final UserIdentifier userId = tokenManager.getUserIdentifier(token);
			final RegisteredUser user = userFacade.findUser(userId);
			request.setAttribute(Constants.USER_REQUEST_ATTRIBUTE, user);
		}
		return returnValue;
	}
}
