package com.duoshouji.server.service.user;

@SuppressWarnings("serial")
public class PasswordNotSetException extends RuntimeException {

	public PasswordNotSetException() {
		super();
	}

	public PasswordNotSetException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PasswordNotSetException(String message, Throwable cause) {
		super(message, cause);
	}

	public PasswordNotSetException(String message) {
		super(message);
	}

	public PasswordNotSetException(Throwable cause) {
		super(cause);
	}
}
