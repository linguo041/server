package com.duoshouji.server.executor;

import com.duoshouji.server.util.UserMessageProxy;
import com.duoshouji.server.util.VerificationCode;
import com.duoshouji.server.util.VerificationCodeGenerator;

public class VerificationCodeAuthenticationExecutor implements VerificationCodeLoginExecutor {

	public static enum State {
		INIT,
		NOTIFIED,
		VERIFIED;
	}
	
	private UserMessageProxy messageProxy;
	private VerificationCodeGenerator codeGenerator;
	private State state;
	private VerificationCode verificationCode;
	
	public VerificationCodeAuthenticationExecutor(UserMessageProxy messageProxy, VerificationCodeGenerator codeGenerator) {
		super();
		this.messageProxy = messageProxy;
		this.codeGenerator = codeGenerator;
		state = State.INIT;
	}

	public void sendVerificationCode() {
		if (state == State.VERIFIED) {
			throw new IllegalStateException("Can't send verificaton code after authentication has completed.");
		}
		verificationCode = codeGenerator.generate();
		messageProxy.sendVerificationCode(verificationCode);
		state = State.NOTIFIED;
	}

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

	public State getState() {
		return state;
	}
}
