package com.duoshouji.server.service.user;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface UserCache {

	RegisteredUser findUser(UserIdentifier accountId);

	void putUser(RegisteredUser user);

}
