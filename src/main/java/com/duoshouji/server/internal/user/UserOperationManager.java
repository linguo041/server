package com.duoshouji.server.internal.user;

import com.duoshouji.server.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.internal.executor.DelegatedVerificationCodeLoginExecutor;
import com.duoshouji.server.internal.executor.SmsVerificationCodeAuthenticationExecutor;
import com.duoshouji.server.util.MessageProxyFactory;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCodeGenerator;

class UserOperationManager {
	
	private VerificationCodeGenerator codeGenerator;
	private MessageProxyFactory messageProxyFactory;

	boolean verifyPassword(OperationDelegatingMobileUser user, Password password) {
		return false;
	}

	VerificationCodeLoginExecutor newVerificationCodeLoginExecutor(
			OperationDelegatingMobileUser user) {
		return new DelegatedVerificationCodeLoginExecutor(
				new SmsVerificationCodeAuthenticationExecutor(messageProxyFactory.getMessageProxy(user), codeGenerator), user);
	}
}
