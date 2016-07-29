package com.duoshouji.server.service.user;

import com.duoshouji.server.util.MobileNumber;

public interface UserRepository {

	RegisteredUser findUser(MobileNumber mobileNumber);

	RegisteredUser createUser(MobileNumber mobileNumber);

}
