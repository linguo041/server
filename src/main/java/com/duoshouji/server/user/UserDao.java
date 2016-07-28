package com.duoshouji.server.user;

public interface UserDao {

	RegisteredUser findUser(UserIdentifier accountId);

	void saveUser(RegisteredUser user);

	void addUser(RegisteredUser user);

}
