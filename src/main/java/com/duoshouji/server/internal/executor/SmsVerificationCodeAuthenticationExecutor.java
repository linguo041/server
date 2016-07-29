package com.duoshouji.server.internal.executor;

import com.duoshouji.server.service.executor.VerificationCodeAuthenticationExecutor;
import com.duoshouji.server.util.UserMessageProxy;
import com.duoshouji.server.util.VerificationCode;
import com.duoshouji.server.util.VerificationCodeGenerator;

public class SmsVerificationCodeAuthenticationExecutor implements VerificationCodeAuthenticationExecutor {

	private UserMessageProxy messageProxy;
	private VerificationCodeGenerator codeGenerator;
	private State state;
	private VerificationCode verificationCode;
	
	public SmsVerificationCodeAuthenticationExecutor(UserMessageProxy messageProxy, VerificationCodeGenerator codeGenerator) {
		super();
		this.messageProxy = messageProxy;
		this.codeGenerator = codeGenerator;
		state = State.INIT;
	}

	/* (non-Javadoc)
	 * @see com.duoshouji.server.executor.VerificationCodeAuthenticationExecutor#sendVerificationCode()
	 */
	@Override
	public void sendVerificationCode() {
		if (state == State.VERIFIED) {
			throw new IllegalStateException("Can't send verificaton code after authentication has completed.");
		}
		verificationCode = codeGenerator.generate();
		messageProxy.sendVerificationCode(verificationCode);
		state = State.NOTIFIED;
	}

	/* (non-Javadoc)
	 * @see com.duoshouji.server.executor.VerificationCodeAuthenticationExecutor#verify(com.duoshouji.server.util.VerificationCode)
	 */
	@Override
	public boolean verify(VerificationCode verificationCode) {
		if (state == State.INIT) {
			throw new IllegalStateException("Must send verificaton code first.");
		}
		if (state == State.VERIFIED) {
			throw new IllegalStateException("Duplicate verification operations.");
		}
		boolean verified = false;
		if (this.verificationCode.equals(verificationCode)) {
			state = State.VERIFIED;
			verified = true;
		}
		return verified;
	}

	/* (non-Javadoc)
	 * @see com.duoshouji.server.executor.VerificationCodeAuthenticationExecutor#getState()
	 */
	@Override
	public State getState() {
		return state;
	}
}
