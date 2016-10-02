package com.duoshouji.server.service.user;

import com.duoshouji.util.Image;
import com.duoshouji.util.MobileNumber;

public interface BasicUser extends BasicUserAttributes {
	
	MobileNumber getMobileNumber();

	Image getPortrait();
}
