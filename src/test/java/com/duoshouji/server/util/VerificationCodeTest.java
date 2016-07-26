package com.duoshouji.server.util;

import org.junit.Test;

public class VerificationCodeTest {

	@Test(expected=NullPointerException.class)
	public void throwNullPointExceptionWhenProvideNullCodeForStaticMethod() {
		VerificationCode.valueOf(null);
	}
}
