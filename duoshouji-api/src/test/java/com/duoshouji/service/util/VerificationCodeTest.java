package com.duoshouji.service.util;

import org.junit.Test;

import com.duoshouji.service.util.VerificationCode;

public class VerificationCodeTest {

	@Test(expected=NullPointerException.class)
	public void throwNullPointExceptionWhenProvideNullCodeForStaticMethod() {
		VerificationCode.valueOf(null);
	}
}
