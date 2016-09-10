package com.duoshouji.server.util;

@SuppressWarnings("serial")
public class VerificationCodeNotMatchException extends RuntimeException {

	public VerificationCodeNotMatchException() {
		super("Wrong verifiction code, please input again");
	}

}
