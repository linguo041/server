package com.duoshouji.server.internal.user;

import org.jvnet.hk2.annotations.Service;

import com.duoshouji.server.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.internal.executor.ExecutorHolder;
import com.duoshouji.server.user.AccountSecurity;
import com.duoshouji.server.user.PasswordNotSetException;
import com.duoshouji.server.user.RegisteredUser;
import com.duoshouji.server.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;

@Service
public class OperationDelegatingMobileUser implements RegisteredUser, AccountSecurity, ExecutorHolder {

	private final UserOperationManager delegator;
	
	private final UserIdentifier userId;
	private final MobileNumber mobileNumber;
	private String passwordDigest;
	private String passwordSalt;
	private VerificationCodeLoginExecutor executor;
	
	public OperationDelegatingMobileUser(MobileNumber mobileNumber, UserOperationManager delegator) {
		super();
		this.mobileNumber = mobileNumber;
		this.userId = new UserIdentifier(mobileNumber);
		this.delegator = delegator;
	}
	
	@Override
	public boolean verifyPassword(Password password) {
		if (!hasPassword()) {
			throw new PasswordNotSetException();
		}
		return delegator.verifyPassword(this, password);
	}
	@Override
	public boolean hasPassword() {
		return passwordDigest != null;
	}
	@Override
	public UserIdentifier getIdentifier() {
		return userId;
	}
	
	@Override
	public AccountSecurity getAccountSecurity() {
		return this;
	}
	
	@Override
	public VerificationCodeLoginExecutor processVerificationCodeLogin() {
		if (executor == null) {
			executor = delegator.newVerificationCodeLoginExecutor(this);
		}
		return executor;
	}
	
	@Override
	public void detachExecutor(Object executor) {
		executor = null;
	}
	
	@Override
	public void setPassword(Password password) {
		passwordDigest = password.toString();
	}
	
	@Override
	public MobileNumber getMobileNumber() {
		return mobileNumber;
	}
}
