package com.duoshouji.restapi.controller.model.response;

import com.duoshouji.service.util.MobileNumber;

public class MobileNumberMappingUserIdResult {
	private MobileNumber mobileNumber;
	private long userId;
	private boolean isFollowing;
	
	public MobileNumberMappingUserIdResult(MobileNumber mobileNumber, long userId, boolean isFollowing) {
		super();
		this.mobileNumber = mobileNumber;
		this.userId = userId;
		this.isFollowing = isFollowing;
	}
	
	public long getMobile() {
		return mobileNumber.toLong();
	}
	
	public long getUserId() {
		return userId;
	}
	
	public boolean getIsFollowing() {
		return isFollowing;
	}
}