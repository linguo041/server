package com.duoshouji.server.internal.user;

import org.jvnet.hk2.annotations.Service;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserCache;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.MobileNumber;

@Service
public class CachedUserRepository implements UserRepository {

	private UserCache userCache;
	private UserRepository delegator;
	
	public CachedUserRepository(UserCache userCache, UserRepository delegator) {
		super();
		this.userCache = userCache;
		this.delegator = delegator;
	}

	@Override
	public RegisteredUser findUser(MobileNumber mobileNumber) {
		return findUser(new UserIdentifier(mobileNumber));
	}
	
	@Override
	public RegisteredUser createUser(MobileNumber mobileNumber) {
		final RegisteredUser user = delegator.createUser(mobileNumber);
		userCache.putUser(user);
		return user;
	}

	@Override
	public RegisteredUser findUser(UserIdentifier userId) {
		RegisteredUser user = userCache.findUser(userId);
		if (user == null) {
			user = delegator.findUser(userId);
			if (user != null) {
				userCache.putUser(user);
			}
		}
		return user;
	}
}
