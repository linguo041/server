package com.duoshouji.server.service.user;

import com.duoshouji.server.util.MobileNumber;

public interface UserFacade {

	RegisteredUser getUser(MobileNumber mobileNumber);
}
