package com.duoshouji.server.service.user;

import com.duoshouji.server.util.MobileNumber;

public interface UserDao {

	RegisteredUserDto findUser(UserIdentifier userId);

	void addUser(UserIdentifier userId, MobileNumber mobileNumber);

}
