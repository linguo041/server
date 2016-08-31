package com.duoshouji.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.duoshouji.server.rest.Constants;
import com.duoshouji.server.util.MobileNumber;

public class MockClient {

	protected final MockMvc mockMvc;
	
	private MockClient(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	public static MockClient newSession(MockMvc mockMvc) {
		return new MockClient(mockMvc);
	}
	
	private static class DefaultSuccessJsonResultMatcher extends SuccessJsonResultMatcher {
		static DefaultSuccessJsonResultMatcher INSTANCE = new DefaultSuccessJsonResultMatcher();
		@Override
		protected void verifyJsonResult(JSONObject json) throws Exception {
			Assert.assertTrue(json.isNull("resultValues"));			
		}
	}
	
	public SendLoginVerificationCode emitSendLoginVerificationCode(MobileNumber mobile) {
		return new SendLoginVerificationCode();
	}
	
	public abstract class ClientRequest {
		protected MvcResult result;
		
		public final ClientRequest perform() throws Exception {
			doPerform();
			return this;
		}
		
		public final ClientRequest expect(ResultMatcher resultMatcher) throws Exception {
			resultMatcher.match(result);
			return this;
		}
		
		protected final void doPerform() throws Exception {
			result = mockMvc.perform(getBuilder())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andReturn();
		}
		
		protected abstract MockHttpServletRequestBuilder getBuilder() throws Exception;
	}

	public abstract class DynamicResourceRequest extends ClientRequest {
		
		public ClientRequest performAndExpectSuccess() throws Exception {
			super.doPerform();
			super.expect(DefaultSuccessJsonResultMatcher.INSTANCE);
			JSONObject json = new JSONObject(result.getResponse().getContentAsString());
			postPerform(json);
			return this;
		}
		
		private void verifyResultCode(JSONObject json) throws JSONException {
			Assert.assertEquals(0, json.getInt("resultCode"));
			Assert.assertTrue(json.isNull("resultErrorMessage"));
		}
		
		protected void verifyJsonResult(JSONObject json) throws JSONException {
			
		}
		
		protected void postPerform(JSONObject json) throws JSONException {
		}
	}
	
	public class SendLoginVerificationCode extends DynamicResourceRequest {

		@Override
		protected MockHttpServletRequestBuilder getBuilder() {
			return post("/accounts/{account-id}/message/verification-code/login", mobile);
		}
	}
	
	public class VerificationCodeLogin extends DynamicResourceRequest {
		private String code;
		private VerificationCodeLogin(String code) {
			super();
			this.code = code;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() {
	    	return post("/accounts/{account-id}/login/verification-code", mobile).param("code", code);
		}
		
		protected void verifyJsonResult(JSONObject json) throws JSONException {
			Assert.assertTrue(json.getJSONObject("resultValues").getBoolean("loginSuccess"));
		}

		@Override
		protected void postPerform(JSONObject json) throws JSONException {
			token = json.getJSONObject("resultValues").getString("token");
		}
	}
	
	public class Logout extends DynamicResourceRequest {

		@Override
		protected MockHttpServletRequestBuilder getBuilder() {
			return post("/accounts/{account-id}/logout", mobile)
					.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token);
		}
		
		@Override
		protected void postPerform(JSONObject json) throws JSONException {
			token = null;
		}		
	}
	
	public class CredentialLogin extends DynamicResourceRequest {

		@Override
		protected MockHttpServletRequestBuilder getBuilder() {
	    	return post("/accounts/{account-id}/login/credential", mobile)
	    			.param("password", MockConstants.MOCK_PASSWORD.toString());
		}
		
		protected void verifyJsonResult(JSONObject json) throws JSONException {
			Assert.assertEquals(0, json.getJSONObject("resultValues").getInt("loginResultCode"));
		}
		
		@Override
		protected void postPerform(JSONObject json) throws JSONException {
			token = json.getJSONObject("resultValues").getString("token");
		}
	}
	
	public class UpdateProfile extends DynamicResourceRequest {
		private String nickName;
		
		public UpdateProfile(String nickName) {
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
		private File imageFile;
		
		public UploadPortrait(File imageFile) {
			super();
			this.imageFile = imageFile;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws FileNotFoundException, IOException {
			return fileUpload("/accounts/{account-id}/settings/profile/protrait", mobile)
					.file(new MockMultipartFile("image", new FileInputStream(imageFile)));
		}
	}
	
	public class PublishNote extends DynamicResourceRequest {
		private final long[] tags;
		private long noteId;
		
		public PublishNote(long[] tags) {
			super();
			this.tags = tags;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return post("/accounts/{account-id}/notes", mobile)
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token)
				.param("title", buildTitle())
				.param("content", buildContent())
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
		
		@Override
		protected void verifyJsonResult(JSONObject json) throws JSONException {
			Assert.assertFalse(json.getJSONObject("resultValues").isNull("noteId"));
		}

		@Override
		protected void postPerform(JSONObject json) throws JSONException {
			noteId = json.getJSONObject("resultValues").getLong("noteId");
		}
		
		public long getNoteId() {
			return noteId;
		}
	}
	
	public class ListSquareNotes extends DynamicResourceRequest {
		private int loadedSize;
		private int pageSize;
		
		public ListSquareNotes(int loadedSize, int pageSize) {
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
}
