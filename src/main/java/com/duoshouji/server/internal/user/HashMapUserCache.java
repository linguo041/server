package com.duoshouji.server.internal.user;

import java.util.HashMap;

import org.jvnet.hk2.annotations.Service;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserCache;
import com.duoshouji.server.service.user.UserIdentifier;

@Service
public class HashMapUserCache implements UserCache {

	private HashMap<UserIdentifier, RegisteredUser> cache;
	
	public HashMapUserCache() {
		cache = new HashMap<UserIdentifier, RegisteredUser>();
	}
	
	@Override
	public RegisteredUser findUser(UserIdentifier accountId) {
		if (accountId == null) {
			throw new IllegalArgumentException("AccountId can't be null.");
		}
		return cache.get(accountId);
	}

	@Override
	public void putUser(RegisteredUser user) {
		if (user == null) {
			throw new IllegalArgumentException("User can't be null.");
		}
		cache.put(user.getIdentifier(), user);
	}

}
