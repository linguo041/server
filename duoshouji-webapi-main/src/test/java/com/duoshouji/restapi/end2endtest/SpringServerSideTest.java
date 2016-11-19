package com.duoshouji.restapi.end2endtest;

import java.math.BigDecimal;

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

import com.duoshouji.restapi.MockConstants;
import com.duoshouji.restapi.end2endtest.MockSession.PublishNote;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.MobileNumber;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/test-context.xml")
public class SpringServerSideTest {
	
	private static final int DEFAULT_PAGE_SIZE = 10;
	
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

	@Test
	public void duoShouJiEnd2EndTest() throws Exception {
		MockClient mockClient = new MockClient(mockMvc);
		final long[] channelIds = mockClient.emitCommonChannel().performAndExpectSuccess().extract(ValueExtractor.IDS_EXTRACTOR);
		
		MockUser user1 = new MockUser(MobileNumber.valueOf(13661863279l), Gender.MALE);
		MockUser user2 = new MockUser(MobileNumber.valueOf(15618875279l), Gender.FEMALE);
		MockUser user3 = new MockUser(MobileNumber.valueOf(18121199370l), Gender.FEMALE);
		user1.registerAndSetupProfile(mockMvc);
		user2.registerAndSetupProfile(mockMvc);
		user3.registerAndSetupProfile(mockMvc);

		MockSession session1 = user1.credentialLoginAndCreateSession(mockMvc);
		MockSession session2 = user2.credentialLoginAndCreateSession(mockMvc);
		MockSession session3 = user3.credentialLoginAndCreateSession(mockMvc);
		
		final long timestamp1 = System.currentTimeMillis();
		session1.emitWatchUser(user3.getUserId()).performAndExpectSuccess();
		session1.emitListSquareNotes(0, DEFAULT_PAGE_SIZE, timestamp1).perform().expect(emptyNoteList());
		session1.emitListChannelNotes(0, DEFAULT_PAGE_SIZE, timestamp1, channelIds[0]).perform().expect(emptyNoteList());
		session1.emitListWatchedNotes(0, DEFAULT_PAGE_SIZE, timestamp1).perform().expect(emptyNoteList());
		session2.emitListPublishedNotes(0, DEFAULT_PAGE_SIZE, timestamp1).perform().expect(emptyNoteList());
		session3.emitListPublishedNotes(0, DEFAULT_PAGE_SIZE, timestamp1).perform().expect(emptyNoteList());
		
		PublishNote publisher;
		publisher = session2.emitPublishNote(MockNoteContent.CONTENT1);

		final long note1 = publisher.performAndExpectSuccess().extract(ValueExtractor.NOTE_ID_EXTRACTOR);
		session2.emitUploadNoteImages(note1, new Image[]{MockConstants.MOCK_LOGO_IMAGE}).performAndExpectSuccess();
		publisher = session2.emitPublishNote(MockNoteContent.CONTENT2);
		final long note2 = publisher.performAndExpectSuccess().extract(ValueExtractor.NOTE_ID_EXTRACTOR);
		session2.emitUploadNoteImages(note2, new Image[]{MockConstants.MOCK_LOGO_IMAGE}).performAndExpectSuccess();
		publisher = session2.emitPublishNote(MockNoteContent.CONTENT3);
		final long note3 = publisher.performAndExpectSuccess().extract(ValueExtractor.NOTE_ID_EXTRACTOR);
		session2.emitUploadNoteImages(note3, new Image[]{MockConstants.MOCK_LOGO_IMAGE}).performAndExpectSuccess();
		publisher = session3.emitPublishNote(MockNoteContent.CONTENT4);
		final long note4 = publisher.performAndExpectSuccess().extract(ValueExtractor.NOTE_ID_EXTRACTOR);
		session3.emitUploadNoteImages(note4, new Image[]{MockConstants.MOCK_LOGO_IMAGE}).performAndExpectSuccess();
		publisher = session3.emitPublishNote(MockNoteContent.CONTENT5);
		final long note5 = publisher.performAndExpectSuccess().extract(ValueExtractor.NOTE_ID_EXTRACTOR);
		session3.emitUploadNoteImages(note5, new Image[]{MockConstants.MOCK_LOGO_IMAGE}).performAndExpectSuccess();
		publisher = session3.emitPublishNote(MockNoteContent.CONTENT6);
		final long note6 = publisher.performAndExpectSuccess().extract(ValueExtractor.NOTE_ID_EXTRACTOR);
		session3.emitUploadNoteImages(note6, new Image[]{MockConstants.MOCK_LOGO_IMAGE}).performAndExpectSuccess();
		
		final long timestamp2 = System.currentTimeMillis();
		session1.emitListSquareNotes(0, DEFAULT_PAGE_SIZE, timestamp1).perform().expect(emptyNoteList());
		session1.emitListChannelNotes(0, DEFAULT_PAGE_SIZE, timestamp1, channelIds[0]).perform().expect(emptyNoteList());
		session1.emitListWatchedNotes(0, DEFAULT_PAGE_SIZE, timestamp1).perform().expect(emptyNoteList());
		session2.emitListPublishedNotes(0, DEFAULT_PAGE_SIZE, timestamp1).perform().expect(emptyNoteList());
		session3.emitListPublishedNotes(0, DEFAULT_PAGE_SIZE, timestamp1).perform().expect(emptyNoteList());
		session1.emitListSquareNotes(0, DEFAULT_PAGE_SIZE, timestamp2).perform().expect(noteList(note6, note5, note4, note3, note2, note1));
		session1.emitListChannelNotes(0, DEFAULT_PAGE_SIZE, timestamp2, channelIds[0]).perform().expect(noteList(note6, note5, note4, note3, note2, note1));
		session1.emitListChannelNotes(0, DEFAULT_PAGE_SIZE, timestamp2, channelIds[1]).perform().expect(noteList(note6, note5, note4, note3, note2, note1));
		session1.emitListChannelNotes(0, DEFAULT_PAGE_SIZE, timestamp2, channelIds[2]).perform().expect(noteList(note6, note5, note4, note3, note2, note1));
		session1.emitListChannelNotes(0, DEFAULT_PAGE_SIZE, timestamp2, channelIds[3]).perform().expect(noteList(note6, note5, note4, note3, note2, note1));
		session1.emitListWatchedNotes(0, DEFAULT_PAGE_SIZE, timestamp2).perform().expect(noteList(note6, note5, note4));
		session2.emitListPublishedNotes(0, DEFAULT_PAGE_SIZE, timestamp2).perform().expect(noteList(note3, note2, note1));
		session3.emitListPublishedNotes(0, DEFAULT_PAGE_SIZE, timestamp2).perform().expect(noteList(note6, note5, note4));
		session1.emitListSquareNotes(4, DEFAULT_PAGE_SIZE, timestamp2).perform().expect(noteList(note2, note1));
		
		session1.emitLikeNote(note6).performAndExpectSuccess();
		session2.emitLikeNote(note6).performAndExpectSuccess();
		session1.emitDisplayNote(note6).perform().expect(isLikedByCurrentUser());
		session2.emitCommentNote(note6, "comment", MockConstants.MOCK_LONGITUDE, MockConstants.MOCK_LATITUDE, 5).performAndExpectSuccess();
		session3.emitDisplayNote(note6).perform().expect(likeCount(2)).expect(commentCount(1));
		
		session1.emitGetUserProfile().perform().expect(new UserProfileMatcher(user1, BigDecimal.ZERO, 0, 0, 1, 0));
		session2.emitGetUserProfile().perform().expect(new UserProfileMatcher(user2, BigDecimal.ZERO, 3, 0, 0, 0));
		session3.emitGetUserProfile().perform().expect(new UserProfileMatcher(user3, BigDecimal.ZERO, 3, 0, 0, 1));
		
		MockUser user4 = new MockUser(MobileNumber.valueOf(13816918000l), Gender.MALE);
		session1.emitInviteContactsFromAddressBook(new MobileNumber[]{user4.getUserId()}).performAndExpectSuccess();
		user4.registerAndSetupProfile(mockMvc);
		MockSession session4 = user4.credentialLoginAndCreateSession(mockMvc);
		publisher = session4.emitPublishNote(MockNoteContent.CONTENT7);
		final long note7 = publisher.perform().extract(ValueExtractor.NOTE_ID_EXTRACTOR);
		session4.emitUploadNoteImages(note7, new Image[]{MockConstants.MOCK_LOGO_IMAGE}).perform();
		session1.emitListWatchedNotes(0, DEFAULT_PAGE_SIZE, timestamp2).perform().expect(noteList(note6, note5, note4));
		session1.emitListWatchedNotes(0, DEFAULT_PAGE_SIZE, System.currentTimeMillis()).perform().expect(noteList(note7, note6, note5, note4));
		session1.emitGetUserProfile().perform().expect(watchCount(2));
		session4.emitGetUserProfile().perform().expect(fanCount(1));
	}

	
	private NoteIdMatcher emptyNoteList() {
		return NoteIdMatcher.EMPTY_MATCHER;
	}
	
	private NoteIdMatcher noteList(long... noteIds) {
		return new NoteIdMatcher(noteIds);
	}
	
	private NoteLikeCountMatcher likeCount(int likeCount) {
		return new NoteLikeCountMatcher(likeCount);
	}
	
	private NoteIsLikedByCurrentUserMatcher isLikedByCurrentUser() {
		return new NoteIsLikedByCurrentUserMatcher();
	}
	
	private NoteCommentCountMatcher commentCount(int commentCount) {
		return new NoteCommentCountMatcher(commentCount);
	}
	
	private WatchCountMatcher watchCount(int watchCount) {
		return new WatchCountMatcher(watchCount);
	}
	
	private FanCountMatcher fanCount(int fanCount) {
		return new FanCountMatcher(fanCount);
	}
	
	private static class NoteIdMatcher extends SuccessJsonResultMatcher {
		static final NoteIdMatcher EMPTY_MATCHER = new NoteIdMatcher();
		
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
	
	private static class NoteIsLikedByCurrentUserMatcher extends SuccessJsonResultMatcher {
		@Override
		protected void verifyJsonResult(JSONObject json) throws Exception {
			JSONObject jsonNoteDetail = json.getJSONObject("resultValues");
			Assert.assertTrue(jsonNoteDetail.getBoolean("isLikedByVisitor"));
		}		
	}
	
	private static class NoteLikeCountMatcher extends SuccessJsonResultMatcher {
		private final int likeCount;
		
		public NoteLikeCountMatcher(int likeCount) {
			super();
			this.likeCount = likeCount;
		}
		
		@Override
		protected void verifyJsonResult(JSONObject json) throws Exception {
			JSONObject jsonNoteDetail = json.getJSONObject("resultValues");
			Assert.assertEquals(likeCount, jsonNoteDetail.getInt("likeCount"));
		}
	}
	
	private static class NoteCommentCountMatcher extends SuccessJsonResultMatcher {
		private final int commentCount;

		public NoteCommentCountMatcher(int commentCount) {
			super();
			this.commentCount = commentCount;
		}

		@Override
		protected void verifyJsonResult(JSONObject json) throws Exception {
			JSONObject jsonNoteDetail = json.getJSONObject("resultValues");
			Assert.assertEquals(commentCount, jsonNoteDetail.getInt("commentCount"));			
		}		
	}
	
	private static class UserProfileMatcher extends SuccessJsonResultMatcher {
		private MockUser user;
		private BigDecimal revenue;
		private int noteCount;
		private int transactionCount;
		private int watchCount;
		private int fanCount;
		
		public UserProfileMatcher(MockUser mockUser, BigDecimal revenue, int noteCount, int transactionCount, int watchCount, int fanCount) {
			super();
			this.user = mockUser;
			this.revenue = revenue;
			this.noteCount = noteCount;
			this.transactionCount = transactionCount;
			this.watchCount = watchCount;
			this.fanCount = fanCount;
		}

		@Override
		protected void verifyJsonResult(JSONObject json) throws Exception {
			JSONObject jsonProfile = json.getJSONObject("resultValues");
			Assert.assertEquals(user.getUserId().toLong(), jsonProfile.getLong("userId"));
			Assert.assertEquals(user.getNickname(), jsonProfile.getString("nickname"));
			Assert.assertEquals(user.getGender(), Gender.valueOf(jsonProfile.getString("gender")));
			Assert.assertEquals(0, revenue.compareTo(new BigDecimal(jsonProfile.getString("totalRevenue"))));
			Assert.assertEquals(noteCount, jsonProfile.getInt("publishedNoteCount"));
			Assert.assertEquals(transactionCount, jsonProfile.getInt("transactionCount"));
			Assert.assertEquals(watchCount, jsonProfile.getInt("watchCount"));
			Assert.assertEquals(fanCount, jsonProfile.getInt("fanCount"));
		}
	}
	
	private static class WatchCountMatcher extends SuccessJsonResultMatcher {
		private final int watchCount;

		public WatchCountMatcher(int watchCount) {
			super();
			this.watchCount = watchCount;
		}
		
		@Override
		protected void verifyJsonResult(JSONObject json) throws Exception {
			JSONObject jsonProfile = json.getJSONObject("resultValues");
			Assert.assertEquals(watchCount, jsonProfile.getInt("watchCount"));
		}
	}
	
	private static class FanCountMatcher extends SuccessJsonResultMatcher {
		private final int fanCount;

		public FanCountMatcher(int fanCount) {
			super();
			this.fanCount = fanCount;
		}
		
		@Override
		protected void verifyJsonResult(JSONObject json) throws Exception {
			JSONObject jsonProfile = json.getJSONObject("resultValues");
			Assert.assertEquals(fanCount, jsonProfile.getInt("fanCount"));
		}
	}
}

