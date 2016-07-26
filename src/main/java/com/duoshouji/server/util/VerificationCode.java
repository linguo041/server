package com.duoshouji.server.util;

public class VerificationCode {

	private final String code;

	private VerificationCode(String code) {
		super();
		this.code = code;
	}
	
	public static VerificationCode valueOf(final String verificationCode) {
		if (verificationCode == null) {
			throw new NullPointerException("VerificationCode can't be null!");
		}
		return new VerificationCode(verificationCode);
	}
	
	@Override
	public String toString() {
		return code;
	}

	@Override
	public int hashCode() {
		return code.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VerificationCode))
			return false;
		VerificationCode other = (VerificationCode) obj;
		return code.equals(other.code);
	}
}
