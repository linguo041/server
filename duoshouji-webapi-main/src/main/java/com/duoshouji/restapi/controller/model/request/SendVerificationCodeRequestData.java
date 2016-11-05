package com.duoshouji.restapi.controller.model.request;

import com.duoshouji.service.util.MobileNumber;

public class SendVerificationCodeRequestData {

	private MobileNumber mobile;

	public void setMobile(String mobile) {
		this.mobile = MobileNumber.valueOf(mobile);
	}

	public MobileNumber getMobile() {
		return mobile;
	}
}
