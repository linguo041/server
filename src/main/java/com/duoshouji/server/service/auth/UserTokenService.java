package com.duoshouji.server.service.auth;

import com.duoshouji.server.util.MobileNumber;

public interface UserTokenService {

	boolean verify(MobileNumber mobileNumber, String token);

	String newToken(MobileNumber mobileNumber);

	void logout(MobileNumber mobileNumber);
}
