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
