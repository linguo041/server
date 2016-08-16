package com.duoshouji.server.internal.user;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;

public interface UserRepository {

	RegisteredUser findUser(MobileNumber mobileNumber);

	RegisteredUser findUser(UserIdentifier userId);
	
	RegisteredUser createUser(MobileNumber mobileNumber);

}
