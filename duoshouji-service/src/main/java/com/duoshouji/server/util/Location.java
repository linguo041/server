package com.duoshouji.server.util;

import java.math.BigDecimal;

public class Location {

	private BigDecimal longitude;
	private BigDecimal latitude;
	
	public Location(BigDecimal longitude, BigDecimal latitude) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
	}
	
	/**
	 * @exception IllegalArgumentException
	 */
	public static Location parse(String coordinate) {
		if (coordinate == null) {
			throw new IllegalArgumentException("Location coordinate can't be null.");
		}
		if (coordinate.charAt(0) != '(' || coordinate.charAt(coordinate.length() - 1) != ')') {
			throw new IllegalArgumentException("Location coordinate must be embraced with bracket.");
		}
		return new Location(
				new BigDecimal(coordinate.substring(1, coordinate.indexOf(',')))
				, new BigDecimal(coordinate.substring(coordinate.indexOf(',') + 1, coordinate.length() - 1)));
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}
	
}
