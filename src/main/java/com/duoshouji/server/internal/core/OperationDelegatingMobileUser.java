package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.service.user.PasswordNotSetException;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.UserMessageProxy;

public class OperationDelegatingMobileUser extends InMemoryBasicUserAttributes implements RegisteredUser {

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
	public int hashCode() {
		return getMobileNumber().hashCode();
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
		return getMobileNumber().equals(other.getMobileNumber());
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
	public void setPassword(Password password) {
		delegator.setPassword(this, password);
	}
	
	@Override
	public void setNickname(String nickname) {
		delegator.setNickname(this, nickname);
	}

	@Override
	public NoteCollection getPublishedNotes() {
		return delegator.getPublishedNotes(this);
	}

	@Override
	public long publishNote(NotePublishAttributes notePublishAttributes) {
		return delegator.publishNote(notePublishAttributes, this);
	}

}

