package com.duoshouji.restapi.auth;


public interface UserTokenService {

	boolean verify(long userId, String token);

	String newToken(long userId);
	
	void logout(long userId);
}
