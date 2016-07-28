package com.duoshouji.server.user;

import org.jvnet.hk2.annotations.Contract;

import com.duoshouji.server.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.util.Identifiable;

@Contract
public interface RegisteredUser extends Identifiable {

	UserIdentifier getIdentifier();
	
	AccountSecurity getAccountSecurity();

	VerificationCodeLoginExecutor processVerificationCodeLogin();

}
