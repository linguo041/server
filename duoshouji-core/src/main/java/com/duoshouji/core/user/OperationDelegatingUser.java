package com.duoshouji.core.user;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.duoshouji.core.FullFunctionalUser;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.user.Password;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.MobileNumber;

class OperationDelegatingUser implements FullFunctionalUser {

	private final UserRepository userRepository;
	private long userId;
	String nickname;
	Image portrait;
	Gender gender;
	String passwordDigest;
	BigDecimal totalRevenue;
	int publishedNoteCount;
	int transactionCount;
	int followCount;
	List<Long> followers = new LinkedList<Long>();
	
	OperationDelegatingUser(long userId, UserRepository userRepository) {
		this.userId = userId;
		this.userRepository = userRepository;
	}
	
	@Override
	public long getUserId() {
		return userId;
	}

	@Override
	public String getNickname() {
		return nickname;
	}

	@Override
	public Gender getGender() {
		return gender;
	}

	@Override
	public Image getPortrait() {
		return portrait;
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
		return followers.size();
	}

	@Override
	public MobileNumber getMobileNumber() {
		return MobileNumber.valueOf(userId);
	}

	@Override
	public String getPasswordDigest() {
		return passwordDigest;
	}

	@Override
	public boolean isPasswordSet() {
		return passwordDigest != null;
	}

	@Override
	public void setPassword(Password password) {
		userRepository.setPassword(this, password);
		passwordDigest = password.toString();
	}
	
	@Override
	public void setNickname(String nickname) {
		userRepository.setNickname(this, nickname);
		this.nickname = nickname;
	}

	@Override
	public void setPortrait(Image portrait) {
		userRepository.setPortrait(this, portrait);
		this.portrait = portrait;
	}

	@Override
	public void setGender(Gender gender) {
		userRepository.setGender(this, gender);
		this.gender = gender;
	}

	@Override
	public void follow(long followeeId) {
		userRepository.follow(followeeId, this);
		++followCount;
	}

	@Override
	public boolean isFollowedBy(long userId) {
		return followers.contains(Long.valueOf(userId));
	}

	@Override
	public List<FullFunctionalUser> getInviters() {
		return userRepository.getInviters(this);
	}

	@Override
	public void fireInviteFriend(MobileNumber mobileNumber) {
		userRepository.inviteFriend(mobileNumber, this);
	}

	@Override
	public void fireBeingFollowed(long followerId) {
		followers.add(Long.valueOf(followerId));
	}

	@Override
	public void firePublishNote() {
		++publishedNoteCount;
	}
}

