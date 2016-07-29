package com.duoshouji.server.internal.user;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.RegisteredUserDto;
import com.duoshouji.server.service.user.UserAlreadyExistsException;
import com.duoshouji.server.service.user.UserCache;
import com.duoshouji.server.service.user.UserDao;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.MobileNumber;

public class CachedUserRepository implements UserRepository {

	private UserDao userDao;
	private UserCache userCache;
	private UserOperationManager operationManager;
	
	public CachedUserRepository(UserDao userDao, UserCache userCache, UserOperationManager operationManager) {
		super();
		this.userDao = userDao;
		this.userCache = userCache;
		this.operationManager = operationManager;
	}

	@Override
	public RegisteredUser findUser(MobileNumber mobileNumber) {
		return findUser(new UserIdentifier(mobileNumber));
	}

	private RegisteredUser findUserFromDao(UserIdentifier userId) {
		RegisteredUser user = null;
		RegisteredUserDto userDto = userDao.findUser(userId);
		if (userDto != null) {
			user = new OperationDelegatingMobileUser(userDto, operationManager); 
		}
		return user;
	}
	
	@Override
	public RegisteredUser createUser(MobileNumber mobileNumber) {
		final UserIdentifier userId = new UserIdentifier(mobileNumber);
		if (containsUser(userId)) {
			throw new UserAlreadyExistsException("User already exists in system, user id: " + userId);
		}
		OperationDelegatingMobileUser user = new OperationDelegatingMobileUser(
				new InMemoryRegisteredUserDto(userId, mobileNumber), operationManager);
		userDao.addUser(userId, mobileNumber);
		userCache.putUser(user);
		return user;
	}
	
	private boolean containsUser(UserIdentifier userId) {
		return findUser(userId) != null;
	}
	
	private RegisteredUser findUser(UserIdentifier userId) {
		RegisteredUser user = userCache.findUser(userId);
		if (user == null) {
			user = findUserFromDao(userId);
			if (user != null) {
				userCache.putUser(user);
			}
		}
		return user;
	}
}
