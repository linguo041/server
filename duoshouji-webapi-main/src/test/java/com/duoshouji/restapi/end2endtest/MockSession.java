package com.duoshouji.restapi.end2endtest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.duoshouji.restapi.Constants;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.Location;
import com.duoshouji.service.util.MobileNumber;
import com.duoshouji.service.util.VerificationCode;

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
	
	public GetUserProfile emitGetUserProfile() {
		return new GetUserProfile();
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
			return post("/message/verification-code")
					.param("purpose", "setpassword")
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
			return post("/user/settings/security/password")
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
			MockHttpServletRequestBuilder builder = post("/user/settings/personal-information")
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
			return get("/user/profile")
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
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
			return post("/user/notes")
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
		private long timestamp;
		
		protected ListSquareNotes(int loadedSize, int pageSize, long timestamp) {
			this.loadedSize = loadedSize;
			this.pageSize = pageSize;
			this.timestamp = timestamp;
		}
		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return get("/notes")
				.param("loadedSize", Integer.toString(loadedSize))
				.param("pageSize", Integer.toString(pageSize))
				.param("timestamp", Long.toString(timestamp));
		}
	}
	
	public class ListWatchedNotes extends ListSquareNotes {
		
		protected ListWatchedNotes(int loadedSize, int pageSize, long timestamp) {
			super(loadedSize, pageSize, timestamp);
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return super.getBuilder()
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
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
			return get("/notes/{note-id}", noteId);
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
			return post("/notes/{note-id}/comments", noteId)
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
					.param("followeeId", followeeId.toString())
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
			MockHttpServletRequestBuilder builder =
					post("/user/invitations")
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
			for (MobileNumber mobile : contactList) {
				builder.param("mobile", mobile.toString());
			}
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
		protected MockHttpServletRequestBuilder getBuilder() throws FileNotFoundException, IOException {
			return post("/user/settings/personal-information/protrait")
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
					.param("imageUrl", image.getUrl())
					.param("imageWidth", Integer.toString(image.getWidth()))
					.param("imageHeight", Integer.toString(image.getHeight()));
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
			MockHttpServletRequestBuilder builder = post("/notes/{note-id}/images", noteId)
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
					.param("imageCount", Integer.toString(images.length));
			for (Image image : images) {
				builder.param("imageUrl", image.getUrl())
				.param("imageWidth", Integer.toString(image.getWidth()))
				.param("imageHeight", Integer.toString(image.getHeight()));
			}
			return builder;
		}
		
	}
}
