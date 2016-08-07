package com.duoshouji.server.service.user;

import com.duoshouji.server.service.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.util.Identifiable;
import com.duoshouji.server.util.Image;

public interface RegisteredUser extends Identifiable {

	UserIdentifier getIdentifier();
	
	AccountSecurity getAccountSecurity();

	VerificationCodeLoginExecutor processVerificationCodeLogin();

	Image getPortrait();

}
