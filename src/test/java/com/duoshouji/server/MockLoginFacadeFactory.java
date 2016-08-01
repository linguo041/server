package com.duoshouji.server;

import org.glassfish.hk2.api.Factory;

import com.duoshouji.server.internal.login.LoginFacadeImpl;
import com.duoshouji.server.internal.user.CachedUserRepository;
import com.duoshouji.server.internal.user.HashMapUserCache;
import com.duoshouji.server.internal.user.UserFacadeImpl;
import com.duoshouji.server.internal.user.UserOperationManager;
import com.duoshouji.server.internal.util.DigitVerificationCodeGenerator;
import com.duoshouji.server.service.login.LoginFacade;

public class MockLoginFacadeFactory implements Factory<LoginFacade> {
	
	private final LoginFacade loginFacade;
	
	public MockLoginFacadeFactory() {
		UserOperationManager userManager = new UserOperationManager();
		userManager.setCodeGenerator(new DigitVerificationCodeGenerator(6));
		userManager.setMessageProxyFactory(MockMessageSender.INSTANCE);
		userManager.setUserDao(MockUserDao.INSTANCE);
		
		loginFacade = new LoginFacadeImpl(new UserFacadeImpl(new CachedUserRepository(new HashMapUserCache(), userManager)));
	}
	
	@Override
	public LoginFacade provide() {
		return loginFacade;
	}

	@Override
	public void dispose(LoginFacade instance) {
	}

}
