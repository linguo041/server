package com.duoshouji.server.user;

public interface UserCache {

	RegisteredUser findUser(UserIdentifier accountId);

	void putUser(RegisteredUser user);

}
