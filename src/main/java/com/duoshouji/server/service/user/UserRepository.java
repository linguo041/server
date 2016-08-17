package com.duoshouji.server.service.user;

import com.duoshouji.server.util.MobileNumber;

public interface UserRepository {

	RegisteredUser findUser(MobileNumber mobileNumber);

	RegisteredUser findUser(UserIdentifier userId);
	
	RegisteredUser createUser(MobileNumber mobileNumber);

}
