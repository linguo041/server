package com.duoshouji.server;

import org.jvnet.hk2.annotations.Service;

import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.session.SessionManager;

@Service
public class MockSessionManager implements SessionManager {

	private static MockSessionManager instance;
	
	public static final String MOCK_TOKEN = "*";
	
	private MockSessionManager() {
	}
	
	@Override
	public String newToken(UserIdentifier accountId) {
		return MOCK_TOKEN;
	}
	public String findToken(String testAccountId) {
		return MOCK_TOKEN;
	}
	public static MockSessionManager getInstance() {
		if (instance == null) {
			instance = new MockSessionManager();
		}
		return instance;
	}

}
