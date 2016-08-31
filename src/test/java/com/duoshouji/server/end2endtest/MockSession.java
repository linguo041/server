package com.duoshouji.server.end2endtest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.duoshouji.server.rest.Constants;
import com.duoshouji.server.util.MobileNumber;

public class MockSession extends MockClient {

	private MobileNumber mobile;
	private String token;
	
	public MockSession(MockMvc mockMvc, MobileNumber mobile, String token) {
		super(mockMvc);
		this.mobile = mobile;
		this.token = token;
	}
	
	public Logout emitLogout() {
		return new Logout();
	}
	
	public SendResetPasswordVerificationCode emitSendResetPasswordVerificationCode() {
		return new SendResetPasswordVerificationCode();
	}
	
	public ResetPassword emitResetPassword(String code, String password) {
		return new ResetPassword(code, password);
	}
	
	public UpdateProfile emitUpdateProfile(String nickname) {
		return new UpdateProfile(nickname);
	}
	
	public UploadPortrait emitUploadPortrait(InputStream imageFile) {
		return new UploadPortrait(imageFile);
	}
	
	public PublishNote emitPublishNote(long[] tags) {
		return new PublishNote(tags);
	}
	
	public UploadNoteMainImage emitUploadNoteMainImage(long noteId, InputStream imageFile) {
		return new UploadNoteMainImage(noteId, imageFile);
	}
	
	public ListSquareNotes emitListSquareNotes(int loadedSize, int pageSize) {
		return new ListSquareNotes(loadedSize, pageSize);
	}
	
	public ListChannelNotes emitListChannelNotes(int loadedSize, int pageSize, long tagId) {
		return new ListChannelNotes(loadedSize, pageSize, tagId);
	}
	
	public ListPublishedNotes emitListPublishedNotes(int loadedSize, int pageSize) {
		return new ListPublishedNotes(loadedSize, pageSize);
	}
	
	public class Logout extends DynamicResourceRequest {
		
		@Override
		protected MockHttpServletRequestBuilder getBuilder() {
			return post("/accounts/{account-id}/logout", mobile)
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
		}	
	}
	
	public class SendResetPasswordVerificationCode extends DynamicResourceRequest {

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return post("/accounts/{account-id}/message/verification-code/reset-password", mobile)
	    			.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
		}
	}
	
	public class ResetPassword extends DynamicResourceRequest {
		private String code;
		private String password;

		private ResetPassword(String code, String password) {
			super();
			this.code = code;
			this.password = password;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return post("/accounts/{account-id}/settings/security/password", mobile)
	    			.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
	    			.param("code", code)
	    			.param("password", password);
		}
	}
	
	public class UpdateProfile extends DynamicResourceRequest {
		private String nickName;
		
		private UpdateProfile(String nickName) {
			super();
			this.nickName = nickName;
		}
		@Override
		protected MockHttpServletRequestBuilder getBuilder() {
			return post("/accounts/{account-id}/settings/profile", mobile)
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
					.param("nickname", nickName);
		}
	}
	
	public class UploadPortrait extends DynamicResourceRequest {
		private InputStream imageFile;
		
		private UploadPortrait(InputStream imageFile) {
			super();
			this.imageFile = imageFile;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws FileNotFoundException, IOException {
			return fileUpload("/accounts/{account-id}/settings/profile/protrait", mobile)
					.file(new MockMultipartFile("image", imageFile));
		}
	}
	
	public class PublishNote extends DynamicResourceRequest {
		private final String title;
		private final String content;
		private final long[] tags;
		
		private PublishNote(long[] tags) {
			super();
			this.title = buildTitle();
			this.content = buildContent();
			this.tags = tags;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return post("/accounts/{account-id}/notes", mobile)
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
				.param("title", title)
				.param("content", content)
				.param("tag", toString(tags));
		}

		private String buildTitle() {
			return "TestNote" + System.currentTimeMillis();
		}
		
		private String buildContent() {
			StringBuilder contentBuilder = new StringBuilder();
			contentBuilder.append("Purpose: test");
			contentBuilder.append('\n');
			contentBuilder.append("Owner Id: " + mobile);
			contentBuilder.append('\n');
			contentBuilder.append("Create Time: " + System.currentTimeMillis());
			contentBuilder.append('\n');
			contentBuilder.append("Tags: " + StringUtils.join(tags, ','));
			contentBuilder.append('\n');
			return contentBuilder.toString();
		}
		
		private String[] toString(long[] tags) {
			final String[] stringTags = new String[tags.length];
			for (int i = 0; i < tags.length; ++i) {
				stringTags[i] = Long.toString(tags[i]);
			}
			return stringTags;
		}
	}
	
	public class UploadNoteMainImage extends DynamicResourceRequest {
		private long noteId;
		private InputStream imageFile;

		private UploadNoteMainImage(long noteId, InputStream imageFile) {
			this.noteId = noteId;
			this.imageFile = imageFile;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return fileUpload("/notes/{note-id}/images/main-image", noteId)
					.file(new MockMultipartFile("image", imageFile));
		}
		
	}
	
	public class ListSquareNotes extends DynamicResourceRequest {
		private int loadedSize;
		private int pageSize;
		
		private ListSquareNotes(int loadedSize, int pageSize) {
			this.loadedSize = loadedSize;
			this.pageSize = pageSize;
		}
		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return get("/accounts/{account-id}/pushed/notes", mobile)
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
				.param("loadedSize", Integer.toString(loadedSize))
				.param("pageSize", Integer.toString(pageSize));
		}
	}
	
	public class ListChannelNotes extends ListSquareNotes {
		private long tagId;

		private ListChannelNotes(int loadedSize, int pageSize, long tagId) {
			super(loadedSize, pageSize);
			this.tagId = tagId;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return super.getBuilder().param("tagId", Long.toString(tagId));
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
			return get("/accounts/{account-id}/notes", mobile)
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
					.param("loadedSize", Integer.toString(loadedSize))
					.param("pageSize", Integer.toString(pageSize));
		}
	}
}
