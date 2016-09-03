package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.user.BasicUserAttributes;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;

public class InMemoryBasicUserAttributes implements BasicUserAttributes {

	private final MobileNumber mobile;
	String nickname;
	Image portrait;
	
	public InMemoryBasicUserAttributes(MobileNumber mobile) {
		super();
		this.mobile = mobile;
	}

	@Override
	public MobileNumber getMobileNumber() {
		return mobile;
	}

	@Override
	public String getNickname() {
		return nickname;
	}

	@Override
	public Image getPortrait() {
		return portrait;
	}

}
