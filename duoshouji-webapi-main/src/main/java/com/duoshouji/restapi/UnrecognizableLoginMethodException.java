package com.duoshouji.restapi;

@SuppressWarnings("serial")
public class UnrecognizableLoginMethodException extends RuntimeException {

	public UnrecognizableLoginMethodException(LoginMethod loginMethod) {
		super("Login method: "+loginMethod.name()+" is not supported!");
	}
}
