package com.duoshouji.server.internal.core;

import java.math.BigDecimal;

import com.duoshouji.server.service.user.Gender;
import com.duoshouji.server.service.user.FullFunctionalUser;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.UserMessageProxy;

public class MobileNumberUserProxy extends AbstractUser implements FullFunctionalUser {

	private final UserNoteOperationManager operationManager;
	private FullFunctionalUser delegator;
	
	public MobileNumberUserProxy(MobileNumber mobileNumber, UserNoteOperationManager operationManager) {
		super(mobileNumber);
		this.operationManager = operationManager;
	}

	private FullFunctionalUser getRegisteredUser() {
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
	public int getWatchCount() {
		return getRegisteredUser().getWatchCount();
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

	@Override
	public void setGender(Gender gender) {
		getRegisteredUser().setGender(gender);
	}

	@Override
	public void addFan(MobileNumber fanId) {
		getRegisteredUser().addFan(fanId);
	}
}
