package com.duoshouji.server;

import org.glassfish.hk2.api.Factory;

import com.duoshouji.server.service.user.UserDao;

public class MockUserDaoFactory implements Factory<UserDao> {

	private final UserDao userDao = new MockUserDao();
	
	@Override
	public UserDao provide() {
		return userDao;
	}

	@Override
	public void dispose(UserDao instance) {
		
	}

}
