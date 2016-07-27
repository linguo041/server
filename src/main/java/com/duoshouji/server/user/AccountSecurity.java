package com.duoshouji.server.user;

import org.jvnet.hk2.annotations.Contract;

import com.duoshouji.server.util.Password;

@Contract
public interface AccountSecurity {

	void changePassword(Password newPassword);
	
	boolean verifyPassword(Password password);
}
