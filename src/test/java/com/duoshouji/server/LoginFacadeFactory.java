package com.duoshouji.server;

import org.glassfish.hk2.api.Factory;

public class LoginFacadeFactory implements Factory<LoginFacade> {
	
	@Override
	public LoginFacade provide() {
		return MockLoginFacade.getInstance();
	}

	@Override
	public void dispose(LoginFacade instance) {		
	}

}
