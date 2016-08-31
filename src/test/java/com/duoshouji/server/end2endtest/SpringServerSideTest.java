package com.duoshouji.server.end2endtest;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.duoshouji.server.MockConstants;
import com.duoshouji.server.util.MessageProxyFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("test-context.xml")
public class SpringServerSideTest {
	
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

	
	private InputStream getImageBytes(String imageName) throws IOException {
		return getClass().getClassLoader().getResourceAsStream(imageName);
	}

	@Test
	public void duoShouJiEnd2EndTest() throws Exception {
		MockClient client = new MockClient(mockMvc);
		client.emitSendLoginVerificationCode(MockConstants.MOCK_MOBILE_NUMBER).performAndExpectSuccess();
		String token = client.emitVerificationCodeLogin(MockConstants.MOCK_MOBILE_NUMBER, getLastVerificationCodeForMockUser())
				.performAndExpectSuccess()
				.extract(ValueExtractor.TOKEN_EXTRACTOR);
		
		MockSession session = new MockSession(mockMvc, MockConstants.MOCK_MOBILE_NUMBER, token);
		session.emitSendResetPasswordVerificationCode().performAndExpectSuccess();
		session.emitResetPassword(getLastVerificationCodeForMockUser(), MockConstants.MOCK_PASSWORD.toString()).performAndExpectSuccess();
		session.emitLogout().performAndExpectSuccess();
		
		token = client.emitCredentialLogin(MockConstants.MOCK_MOBILE_NUMBER, MockConstants.MOCK_PASSWORD.toString())
			.performAndExpectSuccess()
			.extract(ValueExtractor.TOKEN_EXTRACTOR);
		
		session = new MockSession(mockMvc, MockConstants.MOCK_MOBILE_NUMBER, token);
		session.emitUpdateProfile(MockConstants.MOCK_NICKNAME).performAndExpectSuccess();
		session.emitUploadPortrait(getImageBytes(MockConstants.MOCK_USER_PORTRAIT)).performAndExpectSuccess();
		
		final long note1 = session.emitPublishNote(new long[]{1l, 2l})
			.performAndExpectSuccess()
			.extract(ValueExtractor.NOTE_ID_EXTRACTOR).longValue();
		session.emitUploadNoteMainImage(note1, getImageBytes(MockConstants.MOCK_NOTE_MAIN_IMAGE)).performAndExpectSuccess();
		final long note2 = session.emitPublishNote(new long[]{2l, 3l})
			.performAndExpectSuccess()
			.extract(ValueExtractor.NOTE_ID_EXTRACTOR).longValue();
		session.emitUploadNoteMainImage(note2, getImageBytes(MockConstants.MOCK_NOTE_MAIN_IMAGE)).performAndExpectSuccess();
		final long note3 = session.emitPublishNote(new long[]{3l, 4l})
			.performAndExpectSuccess()
			.extract(ValueExtractor.NOTE_ID_EXTRACTOR).longValue();
		session.emitUploadNoteMainImage(note3, getImageBytes(MockConstants.MOCK_NOTE_MAIN_IMAGE)).performAndExpectSuccess();
		
		session.emitListSquareNotes(2, 2).performAndExpectSuccess().expect(new NoteIdMatcher(note1));
		session.emitListPublishedNotes(2, 2).performAndExpectSuccess().expect(new NoteIdMatcher(note1));
		session.emitListChannelNotes(1, 2, 3l).performAndExpectSuccess().expect(new NoteIdMatcher(note2));
		final long note4 = session.emitPublishNote(new long[]{3l})
			.performAndExpectSuccess()
			.extract(ValueExtractor.NOTE_ID_EXTRACTOR).longValue();
		session.emitUploadNoteMainImage(note4, getImageBytes(MockConstants.MOCK_NOTE_MAIN_IMAGE)).performAndExpectSuccess();
		
		session.emitListSquareNotes(0, 10).performAndExpectSuccess().expect(new NoteIdMatcher(note3, note2, note1));
		session.emitListPublishedNotes(0, 10).performAndExpectSuccess().expect(new NoteIdMatcher(note3, note2, note1));
		session.emitListChannelNotes(0, 10, 3l).performAndExpectSuccess().expect(new NoteIdMatcher(note3, note2));
		session.emitListSquareNotes(-1, 10).performAndExpectSuccess().expect(new NoteIdMatcher(note4, note3, note2, note1));
		session.emitListPublishedNotes(-1, 10).performAndExpectSuccess().expect(new NoteIdMatcher(note4, note3, note2, note1));
		session.emitListChannelNotes(-1, 10, 3l).performAndExpectSuccess().expect(new NoteIdMatcher(note4, note3, note2));
	}


	private String getLastVerificationCodeForMockUser() {
		return ((MockMessageSender) wac.getBean(MessageProxyFactory.class)).findHistory(MockConstants.MOCK_MOBILE_NUMBER).toString();
	}
	
	private static class NoteIdMatcher extends SuccessJsonResultMatcher {
		private long[] noteIds;
		private NoteIdMatcher(long... noteIds) {
			super();
			this.noteIds = noteIds;
		}
		@Override
		protected void verifyJsonResult(JSONObject json)
				throws Exception {
			JSONArray jsonNotes = json.getJSONArray("resultValues");
			Assert.assertEquals(noteIds.length, jsonNotes.length());
			for (int i = 0; i < noteIds.length; ++i) {
				final long actualNoteId = jsonNotes.getJSONObject(i).getLong("noteId");
				Assert.assertEquals(noteIds[i], actualNoteId);
			}
		}
	}
}

