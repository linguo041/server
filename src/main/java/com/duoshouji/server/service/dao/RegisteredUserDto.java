package com.duoshouji.server.service.dao;

import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;

public interface RegisteredUserDto {

	UserIdentifier getUserId();

	MobileNumber getMobileNumber();

	String getPasswordDigest();

	String getPasswordSalt();
	
	Image getPortrait();

}
