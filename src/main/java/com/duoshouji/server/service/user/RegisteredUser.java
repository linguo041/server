package com.duoshouji.server.service.user;

import com.duoshouji.server.util.Identifiable;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;

public interface RegisteredUser extends Identifiable {

	UserIdentifier getIdentifier();
	
	Image getPortrait();

	boolean verifyPassword(Password password);

	boolean hasPassword();

	void setPassword(Password password);

	MobileNumber getMobileNumber();
}
