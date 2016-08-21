package com.duoshouji.server.internal.executor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoshouji.server.internal.core.OperationDelegatingMobileUser;
import com.duoshouji.server.service.executor.SecureAccessFacade;
import com.duoshouji.server.service.executor.SecureChecker;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.util.VerificationCode;
import com.duoshouji.server.util.VerificationCodeGenerator;

@Service
public class SecureAccessFacadeImpl implements SecureAccessFacade {
	
	private List<InnerSecureChecker> checkers;
	private VerificationCodeGenerator codeGenerator;

	@Autowired
	public SecureAccessFacadeImpl(VerificationCodeGenerator codeGenerator) {
		super();
		this.checkers = new LinkedList<InnerSecureChecker>();
		this.codeGenerator = codeGenerator;
	}

	private void detachChecker(InnerSecureChecker checker) {
		Iterator<InnerSecureChecker> ite = checkers.iterator();
		while (ite.hasNext()) {
			if (ite.next() == checker) {
				ite.remove();
				break;
			}
		}
	}

	@Override
	public SecureChecker getSecureChecker(RegisteredUser user) {
		InnerSecureChecker returnValue = null;
		boolean found = false;
		Iterator<InnerSecureChecker> ite = checkers.iterator();
		while (ite.hasNext()) {
			returnValue = ite.next();
			if (returnValue.user.equals(user)) {
				found = true;
				break;
			}
		}
		if (!found) {
			returnValue = new InnerSecureChecker((OperationDelegatingMobileUser)user);
			checkers.add(returnValue);
		}
		return returnValue;
	}
	
	private static enum State {
		INIT,
		NOTIFIED,
		VERIFIED;
	}
	
	private class InnerSecureChecker implements SecureChecker {

		private OperationDelegatingMobileUser user;
		private State state;
		private VerificationCode verificationCode;
		
		public InnerSecureChecker(OperationDelegatingMobileUser user) {
			super();
			state = State.INIT;
			this.user = user;
		}
		
		@Override
		public void sendVerificationCode() {
			if (state == State.VERIFIED) {
				throw new IllegalStateException("Can't send verificaton code after authentication has completed.");
			}
			verificationCode = codeGenerator.generate();
			user.getMessageProxy().sendVerificationCode(verificationCode);
			state = State.NOTIFIED;
		}
		
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
				detachChecker(this);
			}
			return verified;
		}
	}

}
