package com.duoshouji.service.user;

public class Password {

	private final String password;
	
	private Password(String password) {
		super();
		if (password == null) {
			throw new NullPointerException();
		}
		this.password = password;
	}

	public static Password valueOf(final String password) {
		return new Password(password);
	}
	
	@Override
	public String toString() {
		return password;
	}

	@Override
	public int hashCode() {
		return password.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Password))
			return false;
		Password other = (Password) obj;
		return password.equals(other.password);
	}
}
