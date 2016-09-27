package com.duoshouji.server.end2endtest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.math.BigDecimal;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.duoshouji.server.rest.Constants;
import com.duoshouji.server.service.user.Gender;
import com.duoshouji.server.util.Location;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.VerificationCode;

public class MockSession extends MockClient {

	private MobileNumber userId;
	private String token;
	
	public MockSession(MockMvc mockMvc, MobileNumber mobile, String token) {
		super(mockMvc);
		this.userId = mobile;
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
	
	public GetUserProfile emitGetUserProfile() {
		return new GetUserProfile();
	}
	
	public PublishNote emitPublishNote(MockNoteContent content) {
		return new PublishNote(content);
	}
	
	public ListSquareNotes emitListSquareNotes(int loadedSize, int pageSize) {
		return new ListSquareNotes(loadedSize, pageSize);
	}
	
	public ListWatchedNotes emitListWatchedNotes(int loadedSize, int pageSize) {
		return new ListWatchedNotes(loadedSize, pageSize);
	}
	
	public ListChannelNotes emitListChannelNotes(int loadedSize, int pageSize, long channelId) {
		return new ListChannelNotes(loadedSize, pageSize, channelId);
	}
	
	public ListPublishedNotes emitListPublishedNotes(int loadedSize, int pageSize) {
		return new ListPublishedNotes(loadedSize, pageSize);
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
	
	public class Logout extends DynamicResourceRequest {
		
		@Override
		protected MockHttpServletRequestBuilder getBuilder() {
			return post("/accounts/{account-id}/logout", userId)
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
		}	
	}
	
	public class SendResetPasswordVerificationCode extends DynamicResourceRequest {

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return post("/accounts/{account-id}/message/verification-code/reset-password", userId)
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
			return post("/accounts/{account-id}/settings/security/password", userId)
	    			.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
	    			.param("code", code.toString())
	    			.param("password", password);
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
		protected MockHttpServletRequestBuilder getBuilder() {
			MockHttpServletRequestBuilder builder = post("/accounts/{account-id}/settings/profile", userId)
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
			if (nickname != null) {
				builder.param("nickname", nickname);
			}
			if (gender != null) {
				builder.param("gender", gender.toString());
			}
			return builder;
		}
	}
	
	public class GetUserProfile extends DynamicResourceRequest {

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return get("/accounts/{account-id}/profile", userId).header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
		}
	}
	
	public class PublishNote extends DynamicResourceRequest {
		private final String title;
		private final String content;
		private long[] tags;
		private long categoryId, brandId, districtId;
		private BigDecimal price;
		private Location location;
		private int rating;
		
		private PublishNote(MockNoteContent content) {
			super();
			this.title = content.getTitle();
			this.content = content.getContent();
		}

		public void setTags(long[] tags) {
			this.tags = tags;
		}

		public void setCategoryId(long categoryId) {
			this.categoryId = categoryId;
		}

		public void setBrandId(long brandId) {
			this.brandId = brandId;
		}

		public void setDistrictId(long districtId) {
			this.districtId = districtId;
		}

		public void setPrice(BigDecimal price) {
			this.price = price;
		}

		public void setLocation(Location location) {
			this.location = location;
		}

		public void setRating(int rating) {
			this.rating = rating;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return post("/accounts/{account-id}/notes", userId)
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
				.param("categoryId", Long.toString(categoryId))
				.param("brandId", Long.toString(brandId))
				.param("productName", title)
				.param("price", price.toString())
				.param("districtId", Long.toString(districtId))
				.param("rating", Integer.toString(rating))
				.param("longitude", location.getLongitude().toString())
				.param("latitude", location.getLatitude().toString())
				.param("title", title)
				.param("content", content)
				.param("tag", toString(tags));
		}
		
		private String[] toString(long[] tags) {
			final String[] stringTags = new String[tags.length];
			for (int i = 0; i < tags.length; ++i) {
				stringTags[i] = Long.toString(tags[i]);
			}
			return stringTags;
		}
	}
	
	public class ListSquareNotes extends DynamicResourceRequest {
		private int loadedSize;
		private int pageSize;
		
		protected ListSquareNotes(int loadedSize, int pageSize) {
			this.loadedSize = loadedSize;
			this.pageSize = pageSize;
		}
		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return get("/accounts/{account-id}/pushed/notes", userId)
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
				.param("loadedSize", Integer.toString(loadedSize))
				.param("pageSize", Integer.toString(pageSize));
		}
	}
	
	public class ListWatchedNotes extends ListSquareNotes {
		
		protected ListWatchedNotes(int loadedSize, int pageSize) {
			super(loadedSize, pageSize);
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return super.getBuilder().param("filter", "WATCHED");
		}
	}
	
	public class ListChannelNotes extends ListSquareNotes {
		private long channelId;

		private ListChannelNotes(int loadedSize, int pageSize, long channelId) {
			super(loadedSize, pageSize);
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
		
		private ListPublishedNotes(int loadedSize, int pageSize) {
			super();
			this.loadedSize = loadedSize;
			this.pageSize = pageSize;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return get("/accounts/{account-id}/notes", userId)
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
					.param("loadedSize", Integer.toString(loadedSize))
					.param("pageSize", Integer.toString(pageSize));
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
			return get("/notes/{note-id}", noteId).header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
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
			return post("/notes/{note-id}/comment", noteId)
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
					.param("comment", content)
					.param("rating", Integer.toString(rating))
					.param("longitude", longitude.toString())
					.param("latitude", latitude.toString());
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
			return get("/notes/{note-id}/comments", noteId).header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
		}
		
	}
	
	public class WatchUser extends DynamicResourceRequest {
		private MobileNumber followedUserId;
		
		public WatchUser(MobileNumber userId) {
			super();
			this.followedUserId = userId;
		}
		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return post("/accounts/{account-id}/watch", userId)
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
					.param("userId", followedUserId.toString());
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
			return post("/accounts/{account-id}/like")
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
					.param("noteId", Long.toString(noteId));
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
			MockHttpServletRequestBuilder builder =
					post("/accounts/{account-id}/invite")
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
			for (MobileNumber mobile : contactList) {
				builder.param("mobile", mobile.toString());
			}
			return builder;
		}
		
	}
}
