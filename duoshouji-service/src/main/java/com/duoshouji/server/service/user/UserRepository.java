package com.duoshouji.server.service.user;

import com.duoshouji.util.MobileNumber;

public interface UserRepository {

	FullFunctionalUser findUser(MobileNumber mobileNumber);

	boolean isMobileNumberRegistered(MobileNumber mobileNumber);
		
}
