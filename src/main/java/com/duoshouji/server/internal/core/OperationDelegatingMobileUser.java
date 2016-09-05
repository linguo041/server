package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.user.PasswordNotSetException;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.UserMessageProxy;

public class OperationDelegatingMobileUser extends InMemoryBasicUser implements RegisteredUser {

	private final UserNoteOperationManager delegator;
	String passwordDigest;
	
	public OperationDelegatingMobileUser(MobileNumber mobile, UserNoteOperationManager delegator) {
		super(mobile);
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
	public UserMessageProxy getMessageProxy() {
		return delegator.getMessageProxy(this);
	}

	@Override
	public void setPassword(Password password) {
		delegator.setPassword(this, password);
		this.passwordDigest = password.toString();
	}
	
	@Override
	public void setNickname(String nickname) {
		delegator.setNickname(this, nickname);
		this.nickname = nickname;
	}

}

