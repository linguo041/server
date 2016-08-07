package com.duoshouji.server;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.session.TokenManager;

@Service
public class MockTokenManager implements TokenManager {
	
	public static final String MOCK_TOKEN = UUID.randomUUID().toString();
	
	public String findToken(String testAccountId) {
		return MOCK_TOKEN;
	}

	@Override
	public String newToken(UserIdentifier userId) {
		return MOCK_TOKEN;
	}
}
