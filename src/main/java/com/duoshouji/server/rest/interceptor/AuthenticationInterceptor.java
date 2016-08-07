package com.duoshouji.server.rest.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.duoshouji.server.rest.Constants;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.session.TokenManager;

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

	private TokenManager tokenManager;

	@Autowired
	public AuthenticationInterceptor(TokenManager tokenManager) {
		super();
		this.tokenManager = tokenManager;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		final boolean returnValue = super.preHandle(request, response, handler);
		final String token = request.getHeader(Constants.APP_TOKEN_HTTP_HEADER_NAME);
		if (token != null) {
			final UserIdentifier userId = tokenManager.getUserIdentifier(token);
			if (userId != null) {
				request.setAttribute("user", userId);
			}
		}
		return returnValue;
	}
}
