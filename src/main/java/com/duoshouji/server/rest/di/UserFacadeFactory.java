package com.duoshouji.server.rest.di;

import org.glassfish.hk2.api.Factory;

import com.duoshouji.server.user.UserFacade;

public class UserFacadeFactory implements Factory<UserFacade> {

	private UserFacade userFacade;
	
	@Override
	public UserFacade provide() {
		return userFacade;
	}

	@Override
	public void dispose(UserFacade instance) {		
	}

}
