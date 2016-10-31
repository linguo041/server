package com.duoshouji.restapi.controller.model.response;

@SuppressWarnings("serial")
public class WrongPasswordException extends RuntimeException {

	public WrongPasswordException() {
		super("Password is not correct.");
	}
}
