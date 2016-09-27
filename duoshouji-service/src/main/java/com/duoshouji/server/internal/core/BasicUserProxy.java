package com.duoshouji.server.internal.core;

import java.math.BigDecimal;

import com.duoshouji.server.service.user.BasicUser;
import com.duoshouji.server.service.user.FullFunctionalUser;
import com.duoshouji.server.service.user.Gender;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;

public class BasicUserProxy implements FullFunctionalUser {
	
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

	private FullFunctionalUser getRegisteredUser() {
		if (!(delegator instanceof FullFunctionalUser)) {
			delegator = operationManager.loadUserIfNotExists(delegator.getMobileNumber());
		}
		return (FullFunctionalUser) delegator;
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
	public BigDecimal getTotalRevenue() {
		return getRegisteredUser().getTotalRevenue();
	}

	@Override
	public int getPublishedNoteCount() {
		return getRegisteredUser().getPublishedNoteCount();
	}

	@Override
	public int getTransactionCount() {
		return getRegisteredUser().getTransactionCount();
	}

	@Override
	public int getFollowCount() {
		return getRegisteredUser().getFollowCount();
	}

	@Override
	public int getFanCount() {
		return getRegisteredUser().getFanCount();
	}

	@Override
	public Gender getGender() {
		return getRegisteredUser().getGender();
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

	@Override
	public void setGender(Gender gender) {
		getRegisteredUser().setGender(gender);
	}

	@Override
	public void follow(MobileNumber userId) {
		getRegisteredUser().follow(userId);
	}

	@Override
	public void invitePeopleFromAddressBook(MobileNumber[] mobileNumbers) {
		getRegisteredUser().invitePeopleFromAddressBook(mobileNumbers);
	}
}

