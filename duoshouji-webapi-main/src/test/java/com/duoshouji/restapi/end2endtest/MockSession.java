package com.duoshouji.restapi.end2endtest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.duoshouji.restapi.Constants;
import com.duoshouji.restapi.MockConstants;
import com.duoshouji.restapi.image.ImageJsonAdapter;
import com.duoshouji.restapi.image.ImageMark;
import com.duoshouji.restapi.image.UploadNoteImageCallbackData;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.MobileNumber;
import com.duoshouji.service.util.VerificationCode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MockSession extends MockClient {

	private String token;
	
	public MockSession(MockMvc mockMvc, String token) {
		super(mockMvc);
		this.token = token;
	}
	
	public Logout emitLogout() {
		return new Logout();
	}
	
	public SendResetPasswordVerificationCode emitSendResetPasswordVerificationCode() {
		return new SendResetPasswordVerificationCode();
	}
	
	public ResetPassword emitResetPassword(VerificationCode code, String password) {
		return new ResetPassword(code, password);
	}
	
	public UpdateProfile emitUpdateProfile(String nickname, Gender gender) {
		return new UpdateProfile(nickname, gender);
	}
	
	public GetUserProfile emitGetUserProfile(MockUser user) {
		return new GetUserProfile(user);
	}
	
	public PublishNote emitPublishNote(MockNoteContent content) {
		return new PublishNote(content);
	}
	
	public ListSquareNotes emitListSquareNotes(int loadedSize, int pageSize, long timestamp) {
		return new ListSquareNotes(loadedSize, pageSize, timestamp);
	}
	
	public ListWatchedNotes emitListWatchedNotes(int loadedSize, int pageSize, long timestamp) {
		return new ListWatchedNotes(loadedSize, pageSize, timestamp);
	}
	
	public ListChannelNotes emitListChannelNotes(int loadedSize, int pageSize, long timestamp, long channelId) {
		return new ListChannelNotes(loadedSize, pageSize, timestamp, channelId);
	}
	
	public ListPublishedNotes emitListPublishedNotes(int loadedSize, int pageSize, long timestamp) {
		return new ListPublishedNotes(loadedSize, pageSize, timestamp);
	}
	
	public DisplayNote emitDisplayNote(long noteId) {
		return new DisplayNote(noteId);
	}
	
	public CommentNote emitCommentNote(long noteId, String content, BigDecimal longitude, BigDecimal latitude, int rating) {
		return new CommentNote(noteId, content, longitude, latitude, rating);
	}
	
	public WatchUser emitWatchUser(MobileNumber userId) {
		return new WatchUser(userId);
	}
	
	public DisplayComment emitDisplayComment(long noteId) {
		return new DisplayComment(noteId);
	}
		
	public LikeNote emitLikeNote(long noteId) {
		return new LikeNote(noteId);
	}
	
	public InviteContactsFromAddressBook emitInviteContactsFromAddressBook(MobileNumber[] contactList) {
		return new InviteContactsFromAddressBook(contactList);
	}
	
	public UploadPortrait emitUploadPortrait(Image image) {
		return new UploadPortrait(image);
	}
	
	public UploadNoteImages emitUploadNoteImages(long noteId, Image[] images) {
		return new UploadNoteImages(noteId, images);
	}
	
	public class Logout extends DynamicResourceRequest {
		
		@Override
		protected MockHttpServletRequestBuilder getBuilder() {
			return post("/user/logout")
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
		}	
	}
	
	public class SendResetPasswordVerificationCode extends DynamicResourceRequest {

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return post("/user/message/verification-code").contentType(MediaType.APPLICATION_JSON)
	    			.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
		}
	}
	
	public class ResetPassword extends DynamicResourceRequest {
		private VerificationCode code;
		private String password;

		private ResetPassword(VerificationCode code, String password) {
			super();
			this.code = code;
			this.password = password;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			Map<String, Object> requestData = new HashMap<String, Object>();
			requestData.put("code", code.toString());
			requestData.put("password", password);
			return post("/user/settings/security/password")
					.contentType(MediaType.APPLICATION_JSON)
					.content(new ObjectMapper().writeValueAsString(requestData))
	    			.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
		}
	}
	
	public class UpdateProfile extends DynamicResourceRequest {
		private String nickname;
		private Gender gender;
		
		private UpdateProfile(String nickname, Gender gender) {
			super();
			this.nickname = nickname;
			this.gender = gender;
		}
		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			Map<String, Object> requestData = new HashMap<String, Object>();
			requestData.put("nickname", nickname);
			requestData.put("gender", gender.name());
			MockHttpServletRequestBuilder builder = post("/user/settings/personal-information")
					.contentType(MediaType.APPLICATION_JSON)
					.content(Utils.getJsonString(requestData))
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
			return builder;
		}
	}
	
	public class GetUserProfile extends DynamicResourceRequest {

		MockUser user;
		
		public GetUserProfile(MockUser user) {
			this.user = user;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			MockHttpServletRequestBuilder builder = get("/user/profile")
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
			if (user != null) {
				builder = builder.param("userId", user.getUserId().toString());
			}
			return builder;
		}
	}
	
	public class PublishNote extends DynamicResourceRequest {
		private final String title;
		private final String content;
		private String address = "address1";
		private BigDecimal longitude = MockConstants.MOCK_LONGITUDE, latitude = MockConstants.MOCK_LATITUDE;
		
		private PublishNote(MockNoteContent content) {
			super();
			this.title = content.getTitle();
			this.content = content.getContent();
		}
		
		public void setAddress(String address) {
			this.address = address;
		}

		public void setLocation(BigDecimal longitude, BigDecimal latitude) {
			this.longitude = longitude;
			this.latitude = latitude;
		}
		
		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			Map<String, Object> requestData = new HashMap<String, Object>();
			requestData.put("title", title);
			requestData.put("content", content);
			requestData.put("address", address);
			requestData.put("longitude", longitude);
			requestData.put("latitude", latitude);
			return post("/user/notes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(Utils.getJsonString(requestData))
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
		}
	}
	
	public class ListSquareNotes extends DynamicResourceRequest {
		private int loadedSize;
		private int pageSize;
		private long timestamp;
		
		protected ListSquareNotes(int loadedSize, int pageSize, long timestamp) {
			this.loadedSize = loadedSize;
			this.pageSize = pageSize;
			this.timestamp = timestamp;
		}
		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			MockHttpServletRequestBuilder builder = get("/notes")
				.param("loadedSize", Integer.toString(loadedSize))
				.param("pageSize", Integer.toString(pageSize))
				.param("timestamp", Long.toString(timestamp));
			
			if (token != null) {
				builder = builder.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
			}
			return builder;
		}
	}
	
	public class ListWatchedNotes extends ListSquareNotes {
		
		protected ListWatchedNotes(int loadedSize, int pageSize, long timestamp) {
			super(loadedSize, pageSize, timestamp);
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return super.getBuilder()
				.param("myFollowOnly", "");
		}
	}
	
	public class ListChannelNotes extends ListSquareNotes {
		private long channelId;

		private ListChannelNotes(int loadedSize, int pageSize, long timestamp, long channelId) {
			super(loadedSize, pageSize, timestamp);
			this.channelId = channelId;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return super.getBuilder().param("tagId", Long.toString(channelId));
		}
	}
	
	public class ListPublishedNotes extends DynamicResourceRequest {
		private int loadedSize;
		private int pageSize;
		private long timestamp;
		
		private ListPublishedNotes(int loadedSize, int pageSize, long timestamp) {
			super();
			this.loadedSize = loadedSize;
			this.pageSize = pageSize;
			this.timestamp = timestamp;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return get("/user/notes")
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
					.param("loadedSize", Integer.toString(loadedSize))
					.param("pageSize", Integer.toString(pageSize))
					.param("timestamp", Long.toString(timestamp));
		}
	}
	
	public class DisplayNote extends DynamicResourceRequest {
		private long noteId;
		
		public DisplayNote(long noteId) {
			super();
			this.noteId = noteId;
		}
		
		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			MockHttpServletRequestBuilder builder = get("/notes/{note-id}", noteId);
			if (token != null) {
				builder = builder.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
			}
			return builder;
		}
	}
	
	public class CommentNote extends DynamicResourceRequest {
		private long noteId;
		private String content;
		private BigDecimal longitude;
		private BigDecimal latitude;
		private int rating;
		
		
		public CommentNote(long noteId, String content, BigDecimal longitude,
				BigDecimal latitude, int rating) {
			super();
			this.noteId = noteId;
			this.content = content;
			this.longitude = longitude;
			this.latitude = latitude;
			this.rating = rating;
		}


		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			Map<String, Object> requestData = new HashMap<String, Object>();
			requestData.put("comment", content);
			requestData.put("rating", rating);
			requestData.put("longitude", longitude);
			requestData.put("latitude", latitude);
			return post("/notes/{note-id}/comments", noteId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(Utils.getJsonString(requestData))
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
		}
	}
	
	public class DisplayComment extends DynamicResourceRequest {
		private long noteId;
		
		public DisplayComment(long noteId) {
			super();
			this.noteId = noteId;
		}
		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return get("/notes/{note-id}/comments", noteId);
		}
		
	}
	
	public class WatchUser extends DynamicResourceRequest {
		private MobileNumber followeeId;
		
		public WatchUser(MobileNumber followeeId) {
			super();
			this.followeeId = followeeId;
		}
		
		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return post("/user/follows")
					.contentType(MediaType.APPLICATION_JSON)
					.content(Utils.getJsonString(Collections.singletonMap("followeeId", (Object)followeeId.toLong())))
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
		}
	}
	
	public class LikeNote extends DynamicResourceRequest {
		private long noteId;
		
		public LikeNote(long noteId) {
			super();
			this.noteId = noteId;
		}
		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return post("/notes/{note-id}/likes", noteId)
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
		}
	}
	
	public class InviteContactsFromAddressBook extends DynamicResourceRequest {

		private MobileNumber[] contactList;
		
		public InviteContactsFromAddressBook(MobileNumber[] contactList) {
			super();
			this.contactList = contactList;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			Map<String, Object> requestData = new HashMap<String, Object>();
			String[] mobiles = new String[contactList.length];
			for (int i = 0; i < mobiles.length; ++i) {
				mobiles[i] = contactList[i].toString();
			}
			requestData.put("mobiles", mobiles);
			MockHttpServletRequestBuilder builder =
					post("/user/invitations")
					.contentType(MediaType.APPLICATION_JSON)
					.content(Utils.getJsonString(requestData))
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
			return builder;
		}
		
	}
	
	public class UploadPortrait extends DynamicResourceRequest {
		private Image image;
		
		private UploadPortrait(Image image) {
			super();
			this.image = image;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			Map<String, Object> requestData = new HashMap<String, Object>();
			requestData.put("url", image.getUrl());
			requestData.put("width", image.getWidth());
			requestData.put("height", image.getHeight());
			return post("/user/settings/personal-information/portrait")
					.contentType(MediaType.APPLICATION_JSON)
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
					.content(Utils.getJsonString(requestData));
		}
	}
	
	public class UploadNoteImages extends DynamicResourceRequest {
		private long noteId;
		private Image[] images;

		private UploadNoteImages(long noteId, Image[] images) {
			this.noteId = noteId;
			this.images = images;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			UploadNoteImageCallbackData.ImageInfo[] requestData = new UploadNoteImageCallbackData.ImageInfo[images.length];
			for (int i = 0; i < requestData.length; ++i) {
				requestData[i] = new UploadNoteImageCallbackData.ImageInfo();
				requestData[i].imageInfo = new ImageJsonAdapter(images[i]);
				requestData[i].imageMarks = new ImageMark[0];
			}
			return post("/notes/{note-id}/images", noteId)
					.contentType(MediaType.APPLICATION_JSON)
					.content(new ObjectMapper().writeValueAsString(Collections.singletonMap("images", requestData)))
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
		}
		
	}
}
