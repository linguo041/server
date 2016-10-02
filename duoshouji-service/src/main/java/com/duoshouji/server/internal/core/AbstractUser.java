package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.user.BasicUser;
import com.duoshouji.util.MobileNumber;

public abstract class AbstractUser implements BasicUser {

	private final MobileNumber mobileNumber;
	
	public AbstractUser(MobileNumber mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public final MobileNumber getMobileNumber() {
		return mobileNumber;
	}

	@Override
	public int hashCode() {
		return mobileNumber.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BasicUser))
			return false;
		BasicUser that = (BasicUser) obj;
		return getMobileNumber().equals(that.getMobileNumber());
	}
}
