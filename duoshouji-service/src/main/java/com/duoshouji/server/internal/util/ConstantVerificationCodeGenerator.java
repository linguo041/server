package com.duoshouji.server.internal.util;

import org.springframework.stereotype.Service;

import com.duoshouji.server.util.VerificationCode;
import com.duoshouji.server.util.VerificationCodeGenerator;

@Service
public class ConstantVerificationCodeGenerator implements
		VerificationCodeGenerator {

	@Override
	public VerificationCode generate() {
		return VerificationCode.valueOf("1234");
	}

}
