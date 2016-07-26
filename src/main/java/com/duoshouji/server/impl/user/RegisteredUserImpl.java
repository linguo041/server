package com.duoshouji.server.impl.user;

import org.jvnet.hk2.annotations.Service;

import com.duoshouji.server.user.AccountSecurity;
import com.duoshouji.server.user.RegisteredUser;

@Service
public class RegisteredUserImpl implements RegisteredUser {

	public RegisteredUserImpl(String accountId) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public AccountSecurity getAccountSecurity() {
		return new AccountSecurityImpl(this);
	}

}
