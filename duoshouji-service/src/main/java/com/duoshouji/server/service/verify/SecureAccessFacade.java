package com.duoshouji.server.service.verify;

import com.duoshouji.server.service.user.FullFunctionalUser;

public interface SecureAccessFacade {

	SecureChecker getSecureChecker(FullFunctionalUser user);
}
