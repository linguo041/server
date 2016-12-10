package com.duoshouji.restapi.end2endtest;

import org.springframework.test.web.servlet.MockMvc;

import com.duoshouji.restapi.MockConstants;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.util.MobileNumber;
import com.duoshouji.service.util.VerificationCode;

public class MockUser {

	private final MobileNumber userId;
	private String password;
	private String nickname;
	private Gender gender;
	
	public MockUser(MobileNumber userId, Gender gender) {
		super();
		this.userId = userId;
		this.password = "1234";
		this.nickname = "MobileUser" + userId;
		this.gender = gender;
	}

	public MobileNumber getUserId() {
		return userId;
	}

	public String getPassword() {
		return password;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public Gender getGender() {
		return gender;
	}
	
	public void registerAndSetupProfile(MockMvc mockMvc) throws Exception {
		MockClient client = new MockClient(mockMvc);
		client.emitSendLoginVerificationCode(getUserId()).performAndExpectSuccess();
		final LoginResult loginResult = client.emitVerificationCodeLogin(getUserId(), VerificationCode.valueOf("1234"))
				.performAndExpectSuccess()
				.extract(ValueExtractor.LONIN_RESULT_EXTRACTOR);
		MockSession session = new MockSession(mockMvc, loginResult.token);
		session.emitSendResetPasswordVerificationCode().performAndExpectSuccess();
		session.emitResetPassword(VerificationCode.valueOf("1234"), getPassword()).performAndExpectSuccess();
		session.emitUpdateProfile(getNickname(), getGender()).performAndExpectSuccess();
		session.emitUploadPortrait(MockConstants.MOCK_LOGO_IMAGE).performAndExpectSuccess();
		session.emitLogout().performAndExpectSuccess();
	}
	
	public MockSession credentialLoginAndCreateSession(MockMvc mockMvc) throws Exception {
		MockClient client = new MockClient(mockMvc);
		LoginResult loginResult = client.emitCredentialLogin(getUserId(), getPassword())
				.performAndExpectSuccess().extract(ValueExtractor.LONIN_RESULT_EXTRACTOR);
		return new MockSession(mockMvc, loginResult.token);
	}
}
