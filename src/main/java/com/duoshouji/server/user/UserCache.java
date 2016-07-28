package com.duoshouji.server.user;

public interface UserCache {

	RegisteredUser findUser(String accountId);

}
