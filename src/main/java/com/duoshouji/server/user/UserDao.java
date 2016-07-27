package com.duoshouji.server.user;

public interface UserDao {

	RegisteredUser findUser(String accountId);

}
