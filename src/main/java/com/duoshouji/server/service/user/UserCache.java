package com.duoshouji.server.service.user;

public interface UserCache {

	RegisteredUser findUser(UserIdentifier accountId);

	void putUser(RegisteredUser user);

}
