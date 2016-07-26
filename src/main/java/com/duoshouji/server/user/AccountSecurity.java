package com.duoshouji.server.user;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface AccountSecurity {

	void changePassword(String newPassword);
	
	boolean verifyPassword(String password);
}
