package com.duoshouji.server.util;

import org.junit.Assert;
import org.junit.Test;

public class MobileNumberTest {

	@Test
	public void singletonMobileNumber() {
		Assert.assertSame(MobileNumber.valueOf(13661863279l), MobileNumber.valueOf("13661863279"));
	}
}
