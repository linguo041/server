package com.duoshouji.server.service.user;

import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;

public interface BasicUser extends BasicUserAttributes {
	
	MobileNumber getMobileNumber();

	Image getPortrait();
}
