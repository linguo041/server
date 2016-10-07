package com.duoshouji.service.user;

@SuppressWarnings("serial")
public class PasswordNotSetException extends RuntimeException {

	public PasswordNotSetException(long userId) {
		super("Password is not setup for user: " + userId);
	}
}
