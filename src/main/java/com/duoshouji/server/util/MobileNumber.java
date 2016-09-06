package com.duoshouji.server.util;

public class MobileNumber {

	private final String number;

	public MobileNumber(String number) {
		super();
		this.number = number;
	}
	
	@Override
	public String toString() {
		return number;
	}

	public long toLong() {
		return Long.valueOf(number);
	}
	
	public static MobileNumber valueOf(String mobileNumber) {
		return new MobileNumber(mobileNumber);
	}
	
	public static MobileNumber valueOf(long mobileNumber) {
		return new MobileNumber(Long.toString(mobileNumber));
	}
	
	@Override
	public int hashCode() {
		return number.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MobileNumber))
			return false;
		MobileNumber other = (MobileNumber) obj;
		return number.equals(other.number);
	}
}
