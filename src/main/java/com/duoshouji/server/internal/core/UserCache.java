package com.duoshouji.server.internal.core;

import java.util.HashMap;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserIdentifier;

class UserCache {

	private HashMap<UserIdentifier, RegisteredUser> cache;
	
	UserCache() {
		cache = new HashMap<UserIdentifier, RegisteredUser>();
	}
	
	void put(RegisteredUser user) {
		cache.put(user.getIdentifier(), user);
	}
	
	RegisteredUser get(UserIdentifier userId) {
		return cache.get(userId);
	}
}
