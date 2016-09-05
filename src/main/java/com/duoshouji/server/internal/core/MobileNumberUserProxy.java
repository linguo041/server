package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.UserMessageProxy;

public class MobileNumberUserProxy extends AbstractUser implements RegisteredUser {

	private final UserNoteOperationManager operationManager;
	private RegisteredUser delegator;
	
	public MobileNumberUserProxy(MobileNumber mobileNumber, UserNoteOperationManager operationManager) {
		super(mobileNumber);
		this.operationManager = operationManager;
	}

	private RegisteredUser getRegisteredUser() {
		if (delegator == null) {
			delegator = operationManager.loadUserIfNotExists(getMobileNumber());
		}
		return delegator;
	}

	@Override
	public String getNickname() {
		return getRegisteredUser().getNickname();
	}

	@Override
	public Image getPortrait() {
		return getRegisteredUser().getPortrait();
	}

	@Override
	public UserMessageProxy getMessageProxy() {
		return getRegisteredUser().getMessageProxy();
	}

	@Override
	public boolean verifyPassword(Password password) {
		return getRegisteredUser().verifyPassword(password);
	}

	@Override
	public boolean hasPassword() {
		return getRegisteredUser().hasPassword();
	}

	@Override
	public void setPassword(Password password) {
		getRegisteredUser().setPassword(password);
	}

	@Override
	public void setNickname(String nickname) {
		getRegisteredUser().setNickname(nickname);
	}

}
