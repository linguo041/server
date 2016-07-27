package com.duoshouji.server.internal.user;

import org.jvnet.hk2.annotations.Service;

import com.duoshouji.server.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.user.AccountSecurity;
import com.duoshouji.server.user.RegisteredUser;
import com.duoshouji.server.util.Password;

@Service
public class InMemoryUser implements RegisteredUser, AccountSecurity {

	private final String accountId;
	private String passwordDigest;
	private String passwordSalt;
	
	public InMemoryUser(String accountId) {
		this.accountId = accountId;
	}

	void setPasswordDigest(String passwordDigest) {
		this.passwordDigest = passwordDigest;
	}

	void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	@Override
	public void changePassword(Password newPassword) {
	}

	@Override
	public boolean verifyPassword(Password password) {
		return false;
	}

	@Override
	public AccountSecurity getAccountSecurity() {
		return this;
	}

	@Override
	public VerificationCodeLoginExecutor processVerificationCodeLogin() {
		return null;
	}
}
