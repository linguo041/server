package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.user.BasicUser;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.UserMessageProxy;

public class BasicUserProxy implements RegisteredUser {
	
	BasicUser delegator;
	private UserNoteOperationManager operationManager;

	public BasicUserProxy(BasicUser delegator, UserNoteOperationManager operationManager) {
		super();
		this.delegator = delegator;
		this.operationManager = operationManager;
	}

	@Override
	public int hashCode() {
		return delegator.getMobileNumber().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BasicUser))
			return false;
		BasicUser that = (BasicUser) obj;
		return getMobileNumber().equals(that.getMobileNumber());
	}

	private RegisteredUser getRegisteredUser() {
		if (!(delegator instanceof RegisteredUser)) {
			delegator = operationManager.loadUserIfNotExists(delegator.getMobileNumber());
		}
		return (RegisteredUser) delegator;
	}
	
	@Override
	public MobileNumber getMobileNumber() {
		return delegator.getMobileNumber();
	}

	@Override
	public String getNickname() {
		return delegator.getNickname();
	}

	@Override
	public Image getPortrait() {
		return delegator.getPortrait();
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

	@Override
	public void setPortrait(Image portrait) {
		getRegisteredUser().setPortrait(portrait);
	}
}

