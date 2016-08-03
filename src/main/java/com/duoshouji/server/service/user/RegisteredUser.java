package com.duoshouji.server.service.user;

import com.duoshouji.server.service.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.util.Identifiable;

public interface RegisteredUser extends Identifiable {

	UserIdentifier getIdentifier();
	
	AccountSecurity getAccountSecurity();

	VerificationCodeLoginExecutor processVerificationCodeLogin();

}
