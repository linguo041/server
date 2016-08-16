package com.duoshouji.server.internal.user;

import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;

public interface RegisteredUserDto {

	UserIdentifier getUserId();

	MobileNumber getMobileNumber();

	String getPasswordDigest();

	String getPasswordSalt();

	void setPasswordDigest(String passwordDigest);
	
	Image getPortrait();

}
