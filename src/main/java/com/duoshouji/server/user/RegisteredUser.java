package com.duoshouji.server.user;

import org.jvnet.hk2.annotations.Contract;

import com.duoshouji.server.executor.VerificationCodeLoginExecutor;

@Contract
public interface RegisteredUser {

	String getIdentifier();
	
	AccountSecurity getAccountSecurity();

	VerificationCodeLoginExecutor processVerificationCodeLogin();

}
