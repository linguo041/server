package com.duoshouji.server.service.verify;

import com.duoshouji.server.util.MobileNumber;

public interface SecureAccessFacade {

	SecureChecker getSecureChecker(MobileNumber userId);
}
