package com.duoshouji.core.util;

import org.springframework.stereotype.Service;

import com.duoshouji.core.VerificationCodeGenerator;
import com.duoshouji.service.util.VerificationCode;

@Service
public class ConstantVerificationCodeGenerator implements
		VerificationCodeGenerator {

	@Override
	public VerificationCode generate() {
		return VerificationCode.valueOf("1234");
	}

}
