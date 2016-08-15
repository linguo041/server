package com.duoshouji.server.service.user;

import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;

public interface UserFacade {

	RegisteredUser getUser(MobileNumber mobileNumber);

	RegisteredUser getUser(UserIdentifier userId);

	void changePassword(UserIdentifier userId, Password password);
}
