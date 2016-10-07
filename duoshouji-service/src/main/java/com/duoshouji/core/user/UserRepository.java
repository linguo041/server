package com.duoshouji.core.user;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import com.duoshouji.core.FullFunctionalUser;
import com.duoshouji.core.UserDao;
import com.duoshouji.core.dao.dto.UserDto;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.user.Password;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.MobileNumber;

@Service
public class UserRepository {
	
	private UserDao userDao;
	private Map<Long, FullFunctionalUser> userCache = new HashMap<Long, FullFunctionalUser>();
	
	public UserRepository(UserDao userDao) {
		this.userDao = userDao;
	}

	private boolean isUserExists(long userId) {
		return userDao.findUser(userId) != null;
	}
	
	FullFunctionalUser getUser(MobileNumber mobileNumber) {
		return getUser(mobileNumber.toLong());
	}
	
	public FullFunctionalUser getUser(long userId) {
		FullFunctionalUser user = null;
		if (isUserExists(userId)) {
			user = userCache.get(Long.valueOf(userId));
			if (user == null) {
				user = new UserProxy(userId, this);
				userCache.put(Long.valueOf(userId), user);
			}			
		}
		return user;
	}
	
	FullFunctionalUser createUser(MobileNumber mobileNumber) {
		final long userId = mobileNumber.toLong();
		userDao.createUser(userId);
		final FullFunctionalUser user = new UserProxy(userId, this);
		setDefaultPersonalInformation(user);
		userCache.put(Long.valueOf(userId), user);
		return user;
	}
	
	private void setDefaultPersonalInformation(FullFunctionalUser user) {
		user.setNickname("MobileUser" + user.getUserId());
		user.setGender(Gender.UNKNOWN);
		user.setPortrait(new Image(121, 121, "http://images.share68.com/images/common/unknown-portrait.jpg"));
	}

	FullFunctionalUser loadUser(long userId) {
		final OperationDelegatingUser user = new OperationDelegatingUser(userId, this);
		convert(user, userDao.findUser(userId));
		return user;
	}
	
	private void convert(OperationDelegatingUser user, UserDto userDto) {
		user.portrait = userDto.portrait;
		user.passwordDigest = userDto.passwordDigest;
		user.nickname = userDto.nickname;
		user.portrait = userDto.portrait;
		user.gender = userDto.gender;
		user.totalRevenue = ObjectUtils.firstNonNull(userDto.totalRevenue, BigDecimal.ZERO);
		user.publishedNoteCount = userDto.publishedNoteCount;
		user.transactionCount = userDto.transactionCount;
		user.followCount = userDto.watchCount;
		user.fanCount = userDto.fanCount;
	}

	void setPassword(OperationDelegatingUser user, Password password) {
		userDao.savePasswordDigest(user.getUserId(), password.toString());
	}

	void setNickname(OperationDelegatingUser user, String nickname) {
		userDao.saveNickname(user.getUserId(), nickname);
	}

	void setPortrait(OperationDelegatingUser user, Image portrait) {
		userDao.savePortrait(user.getUserId(), portrait);
	}

	void setGender(OperationDelegatingUser user, Gender gender) {
		userDao.saveGender(user.getUserId(), gender);
	}

	void follow(long followeeId, OperationDelegatingUser follower) {
		userDao.saveFollowConnection(follower.getUserId(), followeeId);
		FullFunctionalUser followee = userCache.get(Long.valueOf(followeeId));
		if (followee != null) {
			followee.fireBeingFollowed();
		}
	}
	
	void inviteFriend(MobileNumber mobileNumber, OperationDelegatingUser user) {
		userDao.saveInvitation(user.getUserId(), mobileNumber);
	}

	List<FullFunctionalUser> getInviters(OperationDelegatingUser user) {
		List<Long> inviterIds = userDao.findInviters(user.getUserId());
		List<FullFunctionalUser> users = new LinkedList<FullFunctionalUser>();
		for (long inviterId : inviterIds) {
			users.add(getUser(inviterId));
		}
		return users;
	}
}
