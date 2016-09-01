package com.duoshouji.server.service.user;

@SuppressWarnings("serial")
public class UserNotExistsException extends RuntimeException {

	public UserNotExistsException() {
		super();
	}

	public UserNotExistsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UserNotExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserNotExistsException(String message) {
		super(message);
	}

	public UserNotExistsException(Throwable cause) {
		super(cause);
	}

}
