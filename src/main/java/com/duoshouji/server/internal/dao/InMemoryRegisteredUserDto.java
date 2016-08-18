package com.duoshouji.server.internal.dao;

import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;

public class InMemoryRegisteredUserDto implements RegisteredUserDto {
	
	private final UserIdentifier userId;
	private final MobileNumber mobileNumber;
	private String passwordDigest;
	private String passwordSalt;
	private Image portrait;
	
	public InMemoryRegisteredUserDto(UserIdentifier userId, MobileNumber mobileNumber) {
		super();
		this.userId = userId;
		this.mobileNumber = mobileNumber;
	}

	@Override
	public UserIdentifier getUserId() {
		return userId;
	}
	
	@Override
	public MobileNumber getMobileNumber() {
		return mobileNumber;
	}
	
	@Override
	public String getPasswordDigest() {
		return passwordDigest;
	}
	
	@Override
	public String getPasswordSalt() {
		return passwordSalt;
	}

	@Override
	public void setPasswordDigest(String passwordDigest) {
		this.passwordDigest = passwordDigest;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	@Override
	public Image getPortrait() {
		return portrait;
	}
	
	public void setPortrait(Image portrait) {
		this.portrait = portrait;
	}
}
