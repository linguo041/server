package com.duoshouji.core.user;

import java.math.BigDecimal;
import java.util.List;

import com.duoshouji.core.FullFunctionalUser;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.user.Password;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.MobileNumber;

class UserProxy implements FullFunctionalUser {

	private final UserRepository userRepository;
	private final long userId;
	private FullFunctionalUser delegator;
	
	public UserProxy(long userId, UserRepository userRepository) {
		this.userId = userId;
		this.userRepository = userRepository;
	}

	private FullFunctionalUser getDelegator() {
		if (delegator == null) {
			delegator = userRepository.loadUser(userId);
		}
		return delegator;
	}

	@Override
	public BigDecimal getTotalRevenue() {
		return getDelegator().getTotalRevenue();
	}

	@Override
	public int getPublishedNoteCount() {
		return getDelegator().getPublishedNoteCount();
	}

	@Override
	public int getTransactionCount() {
		return getDelegator().getTransactionCount();
	}

	@Override
	public int getFollowCount() {
		return getDelegator().getFollowCount();
	}

	@Override
	public int getFanCount() {
		return getDelegator().getFanCount();
	}

	@Override
	public long getUserId() {
		return userId;
	}

	@Override
	public String getNickname() {
		return getDelegator().getNickname();
	}

	@Override
	public Gender getGender() {
		return getDelegator().getGender();
	}

	@Override
	public Image getPortrait() {
		return getDelegator().getPortrait();
	}

	@Override
	public MobileNumber getMobileNumber() {
		return getDelegator().getMobileNumber();
	}

	@Override
	public boolean isPasswordSet() {
		return getDelegator().isPasswordSet();
	}

	@Override
	public String getPasswordDigest() {
		return getDelegator().getPasswordDigest();
	}

	@Override
	public void setPassword(Password password) {
		getDelegator().setPassword(password);
	}

	@Override
	public void setGender(Gender gender) {
		getDelegator().setGender(gender);
	}

	@Override
	public void setNickname(String nickname) {
		getDelegator().setNickname(nickname);
	}

	@Override
	public void setPortrait(Image portrait) {
		getDelegator().setPortrait(portrait);
	}

	@Override
	public void follow(long userId) {
		getDelegator().follow(userId);
	}
	
	@Override
	public boolean isFollowedBy(long userId) {
		return getDelegator().isFollowedBy(userId);
	}

	@Override
	public List<FullFunctionalUser> getInviters() {
		return getDelegator().getInviters();
	}

	@Override
	public void fireBeingFollowed(long followerId) {
		getDelegator().fireBeingFollowed(followerId);
	}

	@Override
	public void fireInviteFriend(MobileNumber mobileNumber) {
		getDelegator().fireInviteFriend(mobileNumber);
	}

	@Override
	public void firePublishNote() {
		getDelegator().firePublishNote();
	}
}
