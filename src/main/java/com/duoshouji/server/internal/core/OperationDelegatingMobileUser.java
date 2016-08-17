package com.duoshouji.server.internal.core;

import com.duoshouji.server.internal.dao.RegisteredUserDto;
import com.duoshouji.server.internal.executor.ExecutorHolder;
import com.duoshouji.server.service.user.PasswordNotSetException;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;

public class OperationDelegatingMobileUser implements RegisteredUser, ExecutorHolder {

	private final UserNoteOperationManager delegator;
	
	private RegisteredUserDto userDto;
	
	public OperationDelegatingMobileUser(RegisteredUserDto userDto, UserNoteOperationManager delegator) {
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
		return userDto.getPortrait();
	}
}
