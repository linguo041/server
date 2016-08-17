package com.duoshouji.server.service.executor;

import com.duoshouji.server.service.user.RegisteredUser;

public interface MultiStepProcessSupport {

	VerificationCodeLoginExecutor getVerificationCodeLoginExecutor(RegisteredUser user);
	
}
