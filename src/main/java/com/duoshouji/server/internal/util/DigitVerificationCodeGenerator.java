package com.duoshouji.server.internal.util;

import com.duoshouji.server.util.VerificationCode;
import com.duoshouji.server.util.VerificationCodeGenerator;

public class DigitVerificationCodeGenerator implements VerificationCodeGenerator {

	private final int digitCount;
	
	public DigitVerificationCodeGenerator(int digitCount) {
		this.digitCount = digitCount;
	}
	
	@Override
	public VerificationCode generate() {
		StringBuilder codeBuilder = new StringBuilder();
		for (int i = 0; i < digitCount; ++i) {
			codeBuilder.append(nextRandomDigit());
		}
		return VerificationCode.valueOf(codeBuilder.toString()); 
	}

	private char nextRandomDigit() {
		final int digit = (int) (Math.random() * 10);
		return Character.forDigit(digit, 10);
	}
}
