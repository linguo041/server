package com.duoshouji.server.rest.interceptor;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.duoshouji.server.rest.Constants;
import com.duoshouji.server.rest.login.LoginResult;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.session.TokenManager;

public class LoginReponseAdvice<T> implements ResponseBodyAdvice<T> {
	
	private TokenManager tokenManager;
	
	public LoginReponseAdvice(TokenManager tokenManager) {
		super();
		this.tokenManager = tokenManager;
	}

	@Override
	public boolean supports(MethodParameter returnType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return LoginResult.class.isAssignableFrom(returnType.getClass());
	}

	@Override
	public T beforeBodyWrite(T body, MethodParameter returnType,
			MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		if (request.getURI().getPath().startsWith("/login/authenticate")) {
			final UserIdentifier userId = new UserIdentifier(request.getParameter("mobile"));
			response.addHeader(Constants.APP_TOKEN_HTTP_HEADER_NAME, tokenManager.newToken(userId));
		}
	}
}
