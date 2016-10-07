package com.duoshouji.restapi.auth;

@SuppressWarnings("serial")
public class UnauthenticatedUserException extends Exception {

	public UnauthenticatedUserException(String message) {
		super(message);
	}

}
