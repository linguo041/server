package com.duoshouji.server.internal.user;

import com.duoshouji.server.user.RegisteredUser;
import com.duoshouji.server.user.UserDao;
import com.duoshouji.server.user.UserRepository;

public class UserRepositoryImpl implements UserRepository {

	private UserDao userDao;
	
	public UserRepositoryImpl(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public RegisteredUser findUser(String accountId) {
		return userDao.findUser(accountId);
	}

}
