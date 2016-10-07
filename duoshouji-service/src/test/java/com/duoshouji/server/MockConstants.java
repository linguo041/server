package com.duoshouji.server;

import java.math.BigDecimal;

import com.duoshouji.service.user.Gender;
import com.duoshouji.service.user.Password;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.Location;
import com.duoshouji.service.util.MobileNumber;
import com.duoshouji.service.util.VerificationCode;

public class MockConstants {
	
	public static final VerificationCode MOCK_VERIFICATION_CODE = VerificationCode.valueOf("000000");
	public static final MobileNumber MOCK_MOBILE_NUMBER = new MobileNumber("13661863279");
	public static final Password MOCK_PASSWORD = Password.valueOf("Duo@2016");
	public static final String MOCK_NICKNAME = "znzhang1985";
	public static final Gender MOCK_GENDER = Gender.MALE;
	public static final long MOCK_NOTE_ID = 1l;
	public static final String MOCK_USER_PORTRAIT = "portrait.gif";
	public static final String MOCK_NOTE_MAIN_IMAGE = "note.gif";
	public static final String MOCK_NOTE_TITLE = "MOCK_NOTE_TITLE";
	public static final int MOCK_TAG_ID = 1;
	public static final BigDecimal MOCK_LONGITUDE = new BigDecimal("123.456789");
	public static final BigDecimal MOCK_LATITUDE = new BigDecimal("123.456789");
	public static final Location MOCK_LOCATION = new Location(MOCK_LONGITUDE, MOCK_LATITUDE);
	public static final Image MOCK_LOGO_IMAGE = new Image(100, 100, "https://avatars2.githubusercontent.com/u/19884155?v=3&s=200");
}
