package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.dao.RegisteredUserDto;
import com.duoshouji.server.service.user.PasswordNotSetException;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.UserMessageProxy;

public class OperationDelegatingMobileUser implements RegisteredUser {

	private final UserNoteOperationManager delegator;
	
	RegisteredUserDto userDto;
	
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

	@Override
	public int hashCode() {
		return getIdentifier().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RegisteredUser))
			return false;
		RegisteredUser other = (RegisteredUser) obj;
		return getIdentifier().equals(other.getIdentifier());
	}

	@Override
	public UserMessageProxy getMessageProxy() {
		return delegator.getMessageProxy(this);
	}

	@Override
	public void logout() {
		delegator.logout(this);
	}

	@Override
	public String login() {
		return delegator.login(this);
	}

	@Override
	public void setNickname(String nickname) {
		delegator.setNickname(this, nickname);
	}
	
	void setDtoNickname(String nickname) {
		
	}
}
