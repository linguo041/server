package com.duoshouji.server.service.user;

import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;

public interface BasicUserAttributes {
	
	MobileNumber getMobileNumber();

	String getNickname();
	
	Image getPortrait();
}
