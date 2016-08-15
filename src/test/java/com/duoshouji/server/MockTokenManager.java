package com.duoshouji.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.session.TokenManager;

@Service
public class MockTokenManager implements TokenManager {
	
	private HashMap<UserIdentifier, String> tokens = new HashMap<UserIdentifier, String>();
	
	public String findToken(String userIdString) {
		return tokens.get(new UserIdentifier(userIdString));
	}

	@Override
	public String newToken(UserIdentifier userId) {
		final String token = UUID.randomUUID().toString();
		tokens.put(userId, token);
		return token;
	}

	@Override
	public UserIdentifier getUserIdentifier(String token) {
		UserIdentifier userId = null;
		for (Map.Entry<UserIdentifier, String> entry : tokens.entrySet()) {
			if (entry.getValue().equals(token)) {
				userId = entry.getKey();
			}
		}
		return userId;
	}
}
