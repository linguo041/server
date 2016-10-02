package com.duoshouji.server.internal.core;

import java.math.BigDecimal;

import com.duoshouji.server.service.user.FullFunctionalUser;
import com.duoshouji.server.service.user.Gender;
import com.duoshouji.server.service.user.PasswordNotSetException;
import com.duoshouji.server.util.Password;
import com.duoshouji.util.Image;
import com.duoshouji.util.MobileNumber;

class OperationDelegatingMobileUser extends InMemoryBasicUser implements FullFunctionalUser, FollowAware {

	private final UserNoteOperationManager delegator;
	String passwordDigest;
	BigDecimal totalRevenue;
	int publishedNoteCount;
	int transactionCount;
	int followCount;
	int fanCount;
	
	OperationDelegatingMobileUser(MobileNumber mobile, UserNoteOperationManager delegator) {
		super(mobile);
		this.delegator = delegator;
	}
	
	@Override
	public BigDecimal getTotalRevenue() {
		return totalRevenue;
	}

	@Override
	public int getPublishedNoteCount() {
		return publishedNoteCount;
	}

	@Override
	public int getTransactionCount() {
		return transactionCount;
	}

	@Override
	public int getFollowCount() {
		return followCount;
	}

	@Override
	public int getFanCount() {
		return fanCount;
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
	public void setPassword(Password password) {
		delegator.setPassword(this, password);
		this.passwordDigest = password.toString();
	}
	
	@Override
	public void setNickname(String nickname) {
		delegator.setNickname(this, nickname);
		this.nickname = nickname;
	}

	@Override
	public void setPortrait(Image portrait) {
		delegator.setPortrait(this, portrait);
		this.portrait = portrait;
	}

	@Override
	public void setGender(Gender gender) {
		delegator.setGender(this, gender);
		this.gender = gender;
	}

	@Override
	public void follow(MobileNumber followedId) {
		delegator.buildFollowConnection(followedId, this);
		++followCount;
	}

	@Override
	public void fireBeingFollowed() {
		++fanCount;
	}
	
	@Override
	public void fireActivateFollow() {
		++followCount;
	}

	@Override
	public void invitePeopleFromAddressBook(MobileNumber[] mobileNumbers) {
		delegator.inviteFriends(this, mobileNumbers);
	}
}

