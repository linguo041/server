package com.duoshouji.server.util;

import org.junit.Test;

import com.duoshouji.server.util.VerificationCode;

public class VerificationCodeTest {

	@Test(expected=NullPointerException.class)
	public void throwNullPointExceptionWhenProvideNullCodeForStaticMethod() {
		VerificationCode.valueOf(null);
	}
}
