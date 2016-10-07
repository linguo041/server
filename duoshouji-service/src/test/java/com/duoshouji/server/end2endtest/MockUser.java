package com.duoshouji.server.end2endtest;

import org.junit.Assert;
import org.springframework.test.web.servlet.MockMvc;

import com.duoshouji.server.MockConstants;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.util.MobileNumber;

public class MockUser {

	private final MobileNumber userId;
	private String password;
	private String nickname;
	private Gender gender;
	
	public MockUser(MobileNumber userId, Gender gender) {
		super();
		this.userId = userId;
		this.password = "pwd" + userId;
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
	
	public void registerAndSetupProfile(MockMvc mockMvc, MockMessageSender messageReceiver) throws Exception {
		MockClient client = new MockClient(mockMvc);
		client.emitSendLoginVerificationCode(getUserId()).performAndExpectSuccess();
		final LoginResult loginResult = client.emitVerificationCodeLogin(getUserId(), messageReceiver.findHistory(getUserId()))
				.performAndExpectSuccess()
				.extract(ValueExtractor.LONIN_RESULT_EXTRACTOR);
		Assert.assertEquals(userId.toLong(), loginResult.userId);
		MockSession session = new MockSession(mockMvc, loginResult.userId, loginResult.token);
		session.emitSendResetPasswordVerificationCode().performAndExpectSuccess();
		session.emitResetPassword(messageReceiver.findHistory(getUserId()), getPassword()).performAndExpectSuccess();
		session.emitUpdateProfile(getNickname(), getGender()).performAndExpectSuccess();
		session.emitUploadPortrait(userId, MockConstants.MOCK_LOGO_IMAGE).performAndExpectSuccess();
		session.emitLogout().performAndExpectSuccess();
	}
	
	public MockSession credentialLoginAndCreateSession(MockMvc mockMvc) throws Exception {
		MockClient client = new MockClient(mockMvc);
		LoginResult loginResult = client.emitCredentialLogin(getUserId(), getPassword())
				.performAndExpectSuccess().extract(ValueExtractor.LONIN_RESULT_EXTRACTOR);
		Assert.assertEquals(userId.toLong(), loginResult.userId);
		return new MockSession(mockMvc, loginResult.userId, loginResult.token);
	}
}
