package com.duoshouji.restapi.auth;


public interface UserTokenService {

	String newToken(long userId);
	
	long getUserId(String token);
	
	void logout(long userId);
}
