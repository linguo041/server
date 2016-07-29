package com.duoshouji.server;

import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

public class MockConstants {
	
	public static final VerificationCode MOCK_VERIFICATION_CODE = VerificationCode.valueOf("000000");
	public static final MobileNumber MOCK_MOBILE_NUMBER = new MobileNumber("13661863279");
	public static final Password MOCK_PASSWORD = Password.valueOf("******");
	public static final UserIdentifier MOCK_USER_IDENTIFIER = new UserIdentifier(MOCK_MOBILE_NUMBER);
}
