package com.duoshouji.core.util;

import com.duoshouji.core.VerificationCodeGenerator;
import com.duoshouji.service.util.VerificationCode;

public class DigitVerificationCodeGenerator implements VerificationCodeGenerator {

	private static final int DEFAULT_DIGIT_COUNT = 6;
	
	private final int digitCount;
	
	public DigitVerificationCodeGenerator() {
		this(DEFAULT_DIGIT_COUNT);
	}
	
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
