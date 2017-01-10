package com.duoshouji.core;

import java.util.List;

import com.duoshouji.core.dao.dto.UserDto;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.MobileNumber;


public interface UserDao {
	
	void createUser(long userId);
	
	UserDto findUser(long userId);
	
	List<Long> findInviters(long inviteeId);

	void saveNickname(long userId, String nickname);

	void saveGender(long userId, Gender gender);
	
	void savePasswordDigest(long userId, String passwordDigest);

	void savePortrait(long userId, Image portrait);
	
	void saveFollowConnection(long followerId, long followeeId);
	
	void saveInvitation(long inviterId, MobileNumber inviteeMobileNumber);

	List<Long> findFollowers(long userId);
	
}
