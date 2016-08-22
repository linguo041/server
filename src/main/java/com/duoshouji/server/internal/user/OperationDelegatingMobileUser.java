package com.duoshouji.server.internal.user;

import com.duoshouji.server.internal.executor.ExecutorHolder;
import com.duoshouji.server.service.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.service.user.AccountSecurity;
import com.duoshouji.server.service.user.PasswordNotSetException;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.RegisteredUserDto;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;

public class OperationDelegatingMobileUser implements RegisteredUser, AccountSecurity, ExecutorHolder {

	private final UserOperationManager delegator;
	
	private RegisteredUserDto userDto;
	private VerificationCodeLoginExecutor executor;
	
	public OperationDelegatingMobileUser(RegisteredUserDto userDto, UserOperationManager delegator) {
		super();
		this.userDto = userDto;
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
		return userDto.getPasswordDigest() != null;
	}
	@Override
	public UserIdentifier getIdentifier() {
		return userDto.getUserId();
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
		delegator.setPassword(this, password);
	}
	
	@Override
	public MobileNumber getMobileNumber() {
		return userDto.getMobileNumber();
	}
	
	void setPasswordDigest(String passwordDigest) {
		userDto.setPasswordDigest(passwordDigest);
	}
	
	String getPasswordDigest() {
		return userDto.getPasswordDigest();
	}

	@Override
	public Image getPortrait() {
		// TODO Auto-generated method stub
		return null;
	}
}
