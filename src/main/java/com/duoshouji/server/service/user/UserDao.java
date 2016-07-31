package com.duoshouji.server.service.user;

import org.jvnet.hk2.annotations.Contract;

import com.duoshouji.server.util.MobileNumber;

@Contract
public interface UserDao {

	RegisteredUserDto findUser(UserIdentifier userId);

	void addUser(UserIdentifier userId, MobileNumber mobileNumber);

}
