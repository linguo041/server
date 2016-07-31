package com.duoshouji.server.service.user;

import org.jvnet.hk2.annotations.Contract;

import com.duoshouji.server.util.MobileNumber;

@Contract
public interface UserRepository {

	RegisteredUser findUser(MobileNumber mobileNumber);

	RegisteredUser findUser(UserIdentifier userId);
	
	RegisteredUser createUser(MobileNumber mobileNumber);

}
