package com.duoshouji.server.internal.user;

import java.util.HashMap;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.MobileNumber;

public class CachedUserRepository implements UserRepository {

	private HashMap<UserIdentifier, RegisteredUser> cache;
	private UserRepository delegator;
	
	public CachedUserRepository(UserRepository delegator) {
		super();
		this.delegator = delegator;
		cache = new HashMap<UserIdentifier, RegisteredUser>();
	}

	@Override
	public RegisteredUser findUser(MobileNumber mobileNumber) {
		if (mobileNumber == null) {
			throw new IllegalArgumentException("Mobile number can't be null!");
		}
		return findUser(new UserIdentifier(mobileNumber));
	}
	
	@Override
	public RegisteredUser createUser(MobileNumber mobileNumber) {
		final RegisteredUser user = delegator.createUser(mobileNumber);
		cache.put(user.getIdentifier(), user);
		return user;
	}

	@Override
	public RegisteredUser findUser(UserIdentifier userId) {
		if (userId == null) {
			throw new IllegalArgumentException("UserId can't be null.");
		}
		RegisteredUser user = cache.get(userId);
		if (user == null) {
			user = delegator.findUser(userId);
			if (user != null) {
				cache.put(user.getIdentifier(), user);
			}
		}
		return user;
	}
}
