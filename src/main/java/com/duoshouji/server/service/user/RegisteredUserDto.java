package com.duoshouji.server.service.user;

import com.duoshouji.server.util.MobileNumber;

public interface RegisteredUserDto {

	UserIdentifier getUserId();

	MobileNumber getMobileNumber();

	String getPasswordDigest();

	String getPasswordSalt();

	void setPasswordDigest(String passwordDigest);

}
