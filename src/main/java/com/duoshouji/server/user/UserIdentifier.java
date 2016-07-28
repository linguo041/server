package com.duoshouji.server.user;

import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.StringIdentifier;

public class UserIdentifier extends StringIdentifier {

	public UserIdentifier(String identifier) {
		super(identifier);
	}

	public UserIdentifier(MobileNumber mobileNumber) {
		super(mobileNumber.toString());
	}

	@Override
	public Class<?> getReferringClass() {
		return RegisteredUser.class;
	}

	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserIdentifier))
			return false;
		UserIdentifier other = (UserIdentifier) obj;
		return getValue().equals(other.getValue())
			&& getReferringClass() == other.getReferringClass();
	}
}
