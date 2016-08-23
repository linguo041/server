package com.duoshouji.server.service.verify;

import com.duoshouji.server.service.user.RegisteredUser;

public interface SecureAccessFacade {

	SecureChecker getSecureChecker(RegisteredUser user);
}
