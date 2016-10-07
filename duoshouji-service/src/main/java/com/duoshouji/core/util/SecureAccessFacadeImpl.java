package com.duoshouji.core.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoshouji.core.MessageProxy;
import com.duoshouji.core.SecureAccessFacade;
import com.duoshouji.core.SecureChecker;
import com.duoshouji.core.VerificationCodeGenerator;
import com.duoshouji.service.util.VerificationCode;

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
	public SecureChecker getSecureChecker(long userId) {
		InnerSecureChecker returnValue = null;
		boolean found = false;
		Iterator<InnerSecureChecker> ite = checkers.iterator();
		while (ite.hasNext()) {
			returnValue = ite.next();
			if (returnValue.userId == userId) {
				found = true;
				break;
			}
		}
		if (!found) {
			returnValue = new InnerSecureChecker(userId);
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

		private long userId;
		private State state;
		private VerificationCode verificationCode;
		
		public InnerSecureChecker(long userId) {
			super();
			state = State.INIT;
			this.userId = userId;
		}
		
		@Override
		public void sendVerificationCode(MessageProxy messageProxy) {
			if (state == State.VERIFIED) {
				throw new IllegalStateException("Can't send verificaton code after authentication has completed.");
			}
			verificationCode = codeGenerator.generate();
			messageProxy.sendVerificationCode(verificationCode);
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
