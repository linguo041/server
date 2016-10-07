package com.duoshouji.restapi.controller.model;

import com.duoshouji.service.util.MobileNumber;

public class MobileNumberMappingUserIdResult {
	private MobileNumber mobileNumber;
	private long userId;
	
	public MobileNumberMappingUserIdResult(MobileNumber mobileNumber, long userId) {
		super();
		this.mobileNumber = mobileNumber;
		this.userId = userId;
	}
	
	public long getMobile() {
		return mobileNumber.toLong();
	}
	
	public long getUserId() {
		return userId;
	}
}