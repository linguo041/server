package com.duoshouji.service.user;

import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.MobileNumber;
import com.duoshouji.service.util.VerificationCode;

public interface UserFacade {
	
	public static final long NULL_USER_ID = -1;

	void sendLoginVerificationCode(MobileNumber mobileNumber);
	
	long verificationCodeLogin(MobileNumber mobileNumber, VerificationCode verificationCode);
	
	long passwordLogin(MobileNumber mobileNumber, Password mockPassword);
	
	long getUserId(MobileNumber mobileNumber);
	
	UserProfile getUserProfile(long userId);
	
	UserProfileSetter newUserProfileSetter(long userId);
	
	void setPortrait(long userId, Image image);
	
	void sendResetPasswordVerificationCode(long userId);
	
	boolean resetPassword(long userId, VerificationCode code, Password password);
		
	void buildFollowConnection(long followerId, long followedId);

	void inviteFriends(long userId, MobileNumber[] mobileNumbers);

	public static interface UserProfileSetter {
		
		void setNickname(String nickname);
		
		void setGender(Gender gender);
		
		void commitProfileChanges();
	}
}
