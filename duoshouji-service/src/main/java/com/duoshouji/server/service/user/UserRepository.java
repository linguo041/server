package com.duoshouji.server.service.user;

import com.duoshouji.server.util.MobileNumber;

public interface UserRepository {

	FullFunctionalUser findUser(MobileNumber mobileNumber);
		
}
