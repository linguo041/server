package com.duoshouji.server.internal.executor;

import com.duoshouji.server.executor.VerificationCodeAuthenticationExecutor;
import com.duoshouji.server.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.util.VerificationCode;

public class DelegatedVerificationCodeLoginExecutor implements VerificationCodeLoginExecutor {

	private VerificationCodeAuthenticationExecutor delegator;
	private ExecutorHolder holder;

	public DelegatedVerificationCodeLoginExecutor(VerificationCodeAuthenticationExecutor delegator
			, ExecutorHolder holder) {
		super();
		this.delegator = delegator;
		this.holder = holder;
	}
	
	@Override
	public void sendVerificationCode() {
		delegator.sendVerificationCode();
	}

	@Override
	public boolean authenticate(VerificationCode verificationCode) {
		final boolean verified = delegator.verify(verificationCode);
		if (verified) {
			holder.detachExecutor(this);
		}
		return verified;
	}
}
