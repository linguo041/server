package com.duoshouji.server.impl.user;

import org.jvnet.hk2.annotations.Service;

import com.duoshouji.server.user.AccountSecurity;

@Service
public class AccountSecurityImpl implements AccountSecurity {

	public AccountSecurityImpl(RegisteredUserImpl registeredUserImpl) {
	}

	@Override
	public void changePassword(String newPassword) {
	}

	@Override
	public boolean verifyPassword(String password) {
		return true;
	}

}
