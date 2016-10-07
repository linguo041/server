package com.duoshouji.restapi.controller.model;

@SuppressWarnings("serial")
public class WrongVerificationCodeException extends RuntimeException {

	public WrongVerificationCodeException() {
		super("Verification code is not corrent.");
	}
}
