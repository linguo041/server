package com.duoshouji.server.session;

import com.duoshouji.server.service.user.UserIdentifier;

public interface TokenManager {

	String newToken(UserIdentifier userId);

	UserIdentifier getUserIdentifier(String token);

}
