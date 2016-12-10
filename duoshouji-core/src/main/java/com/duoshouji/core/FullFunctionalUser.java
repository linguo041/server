package com.duoshouji.core;

import java.util.List;

import com.duoshouji.service.annotation.Unique;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.user.Password;
import com.duoshouji.service.user.UserProfile;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.MobileNumber;

@Unique
public interface FullFunctionalUser extends UserProfile {

	MobileNumber getMobileNumber();
	
	boolean isPasswordSet();
	
	String getPasswordDigest();

	void setPassword(Password password);

	void setGender(Gender gender);
	
	void setNickname(String nickname);
	
	void setPortrait(Image portrait);

	void follow(long userId);
	
	List<FullFunctionalUser> getInviters();
	
	void fireBeingFollowed(long followerId);
	
	void fireInviteFriend(MobileNumber mobileNumber);
	
	void firePublishNote();
}
