package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.UserMessageProxy;

public class MobileNumberUserProxy implements RegisteredUser {

	private final MobileNumber mobile;
	private final UserNoteOperationManager operationManager;
	private RegisteredUser delegator;
	
	public MobileNumberUserProxy(MobileNumber mobile, UserNoteOperationManager operationManager) {
		super();
		this.mobile = mobile;
		this.operationManager = operationManager;
	}

	private void ensureDelegatorLoaded() {
		if (delegator == null) {
			delegator = operationManager.loadUser(mobileNumber)
		}
	}
	
	@Override
	public MobileNumber getMobileNumber() {
		return mobile;
	}

	@Override
	public String getNickname() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getPortrait() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserMessageProxy getMessageProxy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyPassword(Password password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPassword() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPassword(Password password) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNickname(String nickname) {
		// TODO Auto-generated method stub

	}

}
