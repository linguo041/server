package com.duoshouji.server.session;

import org.jvnet.hk2.annotations.Contract;

import com.duoshouji.server.service.user.UserIdentifier;

@Contract
public interface SessionManager {

	String newToken(UserIdentifier userId);

}
