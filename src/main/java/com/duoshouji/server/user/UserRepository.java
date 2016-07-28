package com.duoshouji.server.user;

public interface UserRepository {

	RegisteredUser findUser(UserIdentifier userId);

	RegisteredUser createUser(UserIdentifier userId);

}
