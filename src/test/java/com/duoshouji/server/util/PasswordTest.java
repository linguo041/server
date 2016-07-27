package com.duoshouji.server.util;

import org.junit.Test;

public class PasswordTest {

	@Test(expected=NullPointerException.class)
	public void nullInputWillThrowException() {
		Password.valueOf(null);
	}
}
