package com.duoshouji.server.session;

import org.jvnet.hk2.annotations.Contract;

import com.duoshouji.server.user.RegisteredUser;

@Contract
public interface SessionManager {

	String newToken(RegisteredUser user);

}
