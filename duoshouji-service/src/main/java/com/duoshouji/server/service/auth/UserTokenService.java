package com.duoshouji.server.service.auth;

import com.duoshouji.util.MobileNumber;

public interface UserTokenService {

	boolean verify(MobileNumber mobileNumber, String token);

	String newToken(MobileNumber mobileNumber);

	MobileNumber fetchUserId(String token);
	
	void logout(MobileNumber mobileNumber);
}
