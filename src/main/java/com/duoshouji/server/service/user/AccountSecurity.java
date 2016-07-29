package com.duoshouji.server.service.user;

import org.jvnet.hk2.annotations.Contract;

import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;

@Contract
public interface AccountSecurity {
	
	boolean verifyPassword(Password password);

	boolean hasPassword();

	void setPassword(Password password);

	MobileNumber getMobileNumber();
}
