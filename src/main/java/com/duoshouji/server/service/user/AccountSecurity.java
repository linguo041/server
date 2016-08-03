package com.duoshouji.server.service.user;

import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;

public interface AccountSecurity {
	
	boolean verifyPassword(Password password);

	boolean hasPassword();

	void setPassword(Password password);

	MobileNumber getMobileNumber();
}
