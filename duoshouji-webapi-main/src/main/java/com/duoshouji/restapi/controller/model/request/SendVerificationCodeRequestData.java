package com.duoshouji.restapi.controller.model.request;

import com.duoshouji.restapi.MessagePurpose;
import com.duoshouji.service.util.MobileNumber;

public class SendVerificationCodeRequestData {

	private MobileNumber mobile;
	private MessagePurpose purpose;
	
	public void setMobile(String mobile) {
		this.mobile = MobileNumber.valueOf(mobile);
	}
	public void setPurpose(String purpose) {
		this.purpose = MessagePurpose.valueOf(purpose.toUpperCase());
	}
	public MobileNumber getMobile() {
		return mobile;
	}
	public MessagePurpose getPurpose() {
		return purpose;
	}
}
