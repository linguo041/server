package com.duoshouji.server.session;

import com.duoshouji.server.service.user.UserIdentifier;

public interface SessionManager {

	String newToken(UserIdentifier userId);

}
