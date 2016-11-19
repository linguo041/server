package com.duoshouji.service.user;

import com.duoshouji.service.util.Image;

public interface BasicUser {
	
	long getUserId();
	
	String getNickname();

	Gender getGender();

	Image getPortrait();

	boolean isFollowedBy(long userId);
}
