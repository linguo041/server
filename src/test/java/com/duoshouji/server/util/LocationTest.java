package com.duoshouji.server.util;

import java.math.BigDecimal;

import junit.framework.Assert;

import org.junit.Test;

public class LocationTest {

	@Test
	public void parseStringCoordinate() {
		Location location = Location.parse("(12.12334,56.18763)");
		final BigDecimal expectedLongitude = new BigDecimal("12.12334");
		final BigDecimal expectedLatitude = new BigDecimal("56.18763");
		Assert.assertTrue(location.getLongitude().compareTo(expectedLongitude) == 0);
		Assert.assertTrue(location.getLatitude().compareTo(expectedLatitude) == 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void wrongStringFormat() {
		Location.parse("12.12334,56.18763");
	}

	@Test(expected=IllegalArgumentException.class)
	public void nullInputForParsing() {
		Location.parse(null);
	}

}
