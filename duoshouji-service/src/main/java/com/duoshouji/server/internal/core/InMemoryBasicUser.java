package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.user.Gender;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;

class InMemoryBasicUser extends AbstractUser {

	String nickname;
	Image portrait;
	Gender gender;
	
	InMemoryBasicUser(MobileNumber mobileNumber) {
		super(mobileNumber);
	}

	@Override
	public String getNickname() {
		return nickname;
	}

	@Override
	public Image getPortrait() {
		return portrait;
	}

	@Override
	public Gender getGender() {
		return gender;
	}

}
