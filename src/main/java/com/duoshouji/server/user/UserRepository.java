package com.duoshouji.server.user;

public interface UserRepository {

	RegisteredUser findUser(String accountId);

}
