package com.duoshouji.server.internal.executor;

import com.duoshouji.server.executor.VerificationCodeAuthenticationExecutor;
import com.duoshouji.server.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.util.VerificationCode;

public class DelegatedVerificationCodeLoginExecutor implements VerificationCodeLoginExecutor {

	private VerificationCodeAuthenticationExecutor delegator;

	public DelegatedVerificationCodeLoginExecutor(VerificationCodeAuthenticationExecutor delegator) {
		super();
		this.delegator = delegator;
	}
	
	/* (non-Javadoc)
	 * @see com.duoshouji.server.executor.VerificationCodeLoginExecutor#sendVerificationCode()
	 */
	@Override
	public void sendVerificationCode() {
		delegator.sendVerificationCode();
	}

	/* (non-Javadoc)
	 * @see com.duoshouji.server.executor.VerificationCodeLoginExecutor#verify(com.duoshouji.server.util.VerificationCode)
	 */
	@Override
	public boolean verify(VerificationCode verificationCode) {
		return delegator.verify(verificationCode);
	}
	
}
