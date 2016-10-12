package com.duoshouji.server.internal.image;

import org.junit.Assert;
import org.junit.Test;

public class StringFormatTest {

	@Test
	public void formatTwoDecimal() {
		Assert.assertEquals("00", String.format("%02d", 0));
		Assert.assertEquals("01", String.format("%02d", 1));
		Assert.assertEquals("10", String.format("%02d", 10));
		Assert.assertEquals("100", String.format("%02d", 100));
	}
}
