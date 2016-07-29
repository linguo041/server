package com.duoshouji.server;

import org.glassfish.hk2.api.Factory;

import com.duoshouji.server.internal.login.LoginFacadeImpl;
import com.duoshouji.server.service.login.LoginFacade;

public class MockLoginFacadeFactory implements Factory<LoginFacade> {
	
	private LoginFacade loginFacade;
	
	public MockLoginFacadeFactory() {
		loginFacade = new LoginFacadeImpl(new );
	}
	
	@Override
	public LoginFacade provide() {
		return new ;
	}

	@Override
	public void dispose(LoginFacade instance) {
	}

}
