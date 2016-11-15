package com.duoshouji.service.util;

import java.math.BigDecimal;

public class Location {

	private BigDecimal longitude;
	private BigDecimal latitude;

	public Location(BigDecimal longitude, BigDecimal latitude) {
		if (longitude == null || latitude == null) {
			throw new IllegalArgumentException("longitude and latitude must provided in pair!");
		}
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}
}
