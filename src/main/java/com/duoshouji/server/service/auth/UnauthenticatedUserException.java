package com.duoshouji.server.service.auth;

@SuppressWarnings("serial")
public class UnauthenticatedUserException extends Exception {

	public UnauthenticatedUserException(String message) {
		super(message);
	}

}
