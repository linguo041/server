package com.duoshouji.server;

import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

public class MockConstants {
	
	public static final VerificationCode MOCK_VERIFICATION_CODE = VerificationCode.valueOf("000000");
	public static final MobileNumber MOCK_MOBILE_NUMBER = new MobileNumber("13661863279");
	public static final Password MOCK_PASSWORD = Password.valueOf("Duo@2016");
	public static final String MOCK_NICKNAME = "znzhang1985";
	public static final long MOCK_NOTE_ID = 1l;
	public static final Image MOCK_USER_PORTRAIT = new Image(100, 100, "http://www.duoshouji.com/image/logo.jpg");
	public static final Image MOCK_NOTE_MAIN_IMAGE = new Image(100, 100, "http://www.duoshouji.com/image/notemain.jpg");
	public static final String MOCK_NOTE_TITLE = "MOCK_NOTE_TITLE";
	public static final int MOCK_TAG_ID = 1;
}
