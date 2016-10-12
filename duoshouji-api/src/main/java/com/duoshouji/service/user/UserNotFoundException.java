package com.duoshouji.service.user;

import com.duoshouji.service.util.MobileNumber;


@SuppressWarnings("serial")
public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(MobileNumber mobileNumber) {
		super("No user found with mobile number: " + mobileNumber);
	}
}
