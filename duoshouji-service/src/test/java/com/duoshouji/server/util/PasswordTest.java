package com.duoshouji.server.util;

import org.junit.Test;

import com.duoshouji.server.util.Password;

public class PasswordTest {

	@Test(expected=NullPointerException.class)
	public void nullInputWillThrowException() {
		Password.valueOf(null);
	}
}
