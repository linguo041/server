package com.duoshouji.server.internal.user;

import com.duoshouji.server.user.RegisteredUser;
import com.duoshouji.server.user.UserAlreadyExistsException;
import com.duoshouji.server.user.UserCache;
import com.duoshouji.server.user.UserDao;
import com.duoshouji.server.user.UserIdentifier;
import com.duoshouji.server.user.UserRepository;

public class CachedUserRepository implements UserRepository {

	private UserDao userDao;
	private UserCache userCache;
	
	public CachedUserRepository(UserDao userDao, UserCache userCache) {
		super();
		this.userDao = userDao;
		this.userCache = userCache;
	}

	@Override
	public RegisteredUser findUser(UserIdentifier userId) {
		RegisteredUser user = userCache.findUser(userId);
		if (user == null) {
			user = userDao.findUser(userId);
			if (user != null) {
				userCache.putUser(user);
			}
		}
		return user;
	}

	@Override
	public RegisteredUser createUser(UserIdentifier userId) {
		if (containsUser(userId)) {
			throw new UserAlreadyExistsException("User already exists in system, user id: " + userId);
		}
		OperationDelegatingMobileUser user = new OperationDelegatingMobileUser(userId, null);
		userDao.addUser(user);
		userCache.putUser(user);
		return user;
	}
	
	private boolean containsUser(UserIdentifier userId) {
		return findUser(userId) != null;
	}
}
