package com.duoshouji.server.service.user;

import com.duoshouji.server.annotation.Unique;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;

@Unique
public interface FullFunctionalUser extends UserProfile {

	boolean verifyPassword(Password password);

	boolean hasPassword();

	void setPassword(Password password);

	void setGender(Gender gender);
	
	void setNickname(String nickname);
	
	void setPortrait(Image portrait);

	void invitePeopleFromAddressBook(MobileNumber[] mobileNumberOfContacts);

	void follow(MobileNumber userId);
}
