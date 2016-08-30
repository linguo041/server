package com.duoshouji.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.duoshouji.server.rest.Constants;
import com.duoshouji.server.util.MessageProxyFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("test-context.xml")
public class SpringServerSideTest {
	
    @Autowired
    private WebApplicationContext wac;
    private int noteCount;
    private MockMvc mockMvc;

    @Before
    public void setup() {
    	this.noteCount = 0;
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
    
    public String firstLogin() throws Exception {
    	MockHttpServletRequestBuilder requestBuilder;
    	requestBuilder = post("/accounts/"+MockConstants.MOCK_MOBILE_NUMBER+"/message/verification-code/login");
    	mockMvc.perform(requestBuilder)
    	.andExpect(statusIsOk())
    	.andExpect(withJsonValue(null));
    	
    	requestBuilder = post("/accounts/"+MockConstants.MOCK_MOBILE_NUMBER+"/login/verification-code");
    	requestBuilder.param("code", getLastVerificationCodeForMockUser());
    	return getTokenFromLoginResult(mockMvc.perform(requestBuilder).andReturn());
    }
    
    public void setPassword(String userToken) throws Exception {
    	mockMvc.perform(post("/accounts/"+MockConstants.MOCK_MOBILE_NUMBER+"/message/verification-code/reset-password")
    			.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, userToken));
     	mockMvc.perform(
       			post("/accounts/"+MockConstants.MOCK_MOBILE_NUMBER.toString()+"/settings/security/password")
    			.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, userToken)
    			.param("code", getLastVerificationCodeForMockUser())
    			.param("password", MockConstants.MOCK_PASSWORD.toString())
    	)
    	.andExpect(statusIsOk());
    }
    
    public void logout(String userToken) throws Exception {
    	mockMvc.perform(
    			post("/accounts/"+MockConstants.MOCK_MOBILE_NUMBER.toString()+"/logout")
    			.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, userToken))
    			.andExpect(statusIsOk());
    }
    
	public String credentialLogin() throws Exception {
		return getTokenFromLoginResult(
			mockMvc.perform(
				post("/accounts/"+MockConstants.MOCK_MOBILE_NUMBER+"/login/credential")
				.param("mobile", MockConstants.MOCK_MOBILE_NUMBER.toString())
				.param("password", MockConstants.MOCK_PASSWORD.toString())				
			).andReturn()
		);
	}
	
	public void setNickname(String userToken) throws Exception {
		mockMvc.perform(
				post("/accounts/"+MockConstants.MOCK_MOBILE_NUMBER+"/settings/profile")
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, userToken)
				.param("nickname", MockConstants.MOCK_NICKNAME)				
			).andExpect(statusIsOk());
	}
	
	public void uploadUserPortrait(String userToken) throws Exception {
		mockMvc.perform(
			fileUpload("/accounts/{account-id}/settings/profile/protrait", MockConstants.MOCK_MOBILE_NUMBER)
			.file(new MockMultipartFile("image", getImageBytes("portrait.gif")))
		);
	}
	
	public void addNotes(int noteCount, String[] tagIds, String userToken) throws Exception {
		for (int i = 0; i < noteCount; ++i) {
			long noteId = getNoteIdFromReturnValue(mockMvc.perform(post("/accounts/"+MockConstants.MOCK_MOBILE_NUMBER+"/notes")
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, userToken)
				.param("title", "title" + this.noteCount)
				.param("content", "content" + this.noteCount)
				.param("tag", tagIds)
			).andReturn());
	
			mockMvc.perform(
				fileUpload("/notes/{note-id}/images/main-image", noteId)
				.file(new MockMultipartFile("image", getImageBytes("note.gif")))
			);
			this.noteCount++;
		}
	}
	
	private long getNoteIdFromReturnValue(MvcResult returnValue) throws Exception {
		JSONObject loginResult = new JSONObject(returnValue.getResponse().getContentAsString());
		loginResult = loginResult.getJSONObject("resultValues");
		return loginResult.getLong("noteId");
	}
	
	private InputStream getImageBytes(String imageName) throws IOException {
		return getClass().getClassLoader().getResourceAsStream(imageName);
	}
	
	private void checkNoteReturn(String userToken, int loaded, int pageSize, int toIndex, int count) throws Exception {
		JSONObject resultJson = new JSONObject(mockMvc.perform(get("/accounts/"+MockConstants.MOCK_MOBILE_NUMBER+"/pushed/notes")
				.param("loadedSize", Integer.toString(loaded))
				.param("pageSize", Integer.toString(pageSize))
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, userToken))
				.andExpect(statusIsOk())
				.andReturn().getResponse().getContentAsString());
		JSONArray array = resultJson.getJSONArray("resultValues");
		Assert.assertEquals(count, array.length());
		for (int i = 0; i < count; ++i) {
			JSONObject json = array.getJSONObject(i);
			Assert.assertEquals("title" + Integer.toString(toIndex - i), json.getString("title"));
			Assert.assertEquals(1024, json.getInt("imageWidth"));
			Assert.assertEquals(768, json.getInt("imageHeight"));
			final int imageSize = mockMvc.perform(get(json.getString("image")))
					.andReturn().getResponse().getContentAsByteArray().length;
			Assert.assertEquals(228162, imageSize);
		}
	}

	private void checkNoteReturnWithTag2(String userToken) throws Exception {
		JSONObject resultJson = new JSONObject(mockMvc.perform(get("/accounts/"+MockConstants.MOCK_MOBILE_NUMBER+"/pushed/notes")
				.param("tagId", "2")
				.param("loadedSize", "-1")
				.param("pageSize", "10")
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, userToken))
				.andExpect(statusIsOk())
				.andReturn().getResponse().getContentAsString());
		JSONArray array = resultJson.getJSONArray("resultValues");
		Assert.assertEquals(3, array.length());
	}

	
	@Test
	public void duoShouJiEnd2EndTest() throws Exception {
		String userToken = firstLogin();
		setPassword(userToken);
		logout(userToken);
		userToken = credentialLogin();
		setNickname(userToken);
		uploadUserPortrait(userToken);
		addNotes(1, new String[]{"1", "2"}, userToken);
		checkNoteReturn(userToken, -1, 2, 0, 1);
		addNotes(2, new String[]{"2", "3"}, userToken);
		checkNoteReturn(userToken, 1, 2, 1, 0);
		checkNoteReturn(userToken, -1, 2, 2, 2);
		checkNoteReturn(userToken, 2, 2, 0, 1);
		checkNoteReturnWithTag2(userToken);
		logout(userToken);
	}

	private String getTokenFromLoginResult(MvcResult result) throws Exception {
		JSONObject loginResult = new JSONObject(result.getResponse().getContentAsString());
		loginResult = loginResult.getJSONObject("resultValues");
		return loginResult.getString("token");
	}
	
	private String getLastVerificationCodeForMockUser() {
		return ((MockMessageSender) wac.getBean(MessageProxyFactory.class)).findHistory(MockConstants.MOCK_MOBILE_NUMBER).toString();
	}
	
	private static ResultMatcher statusIsOk() {
		return status().isOk();
	}
	
	private static ResultMatcher withJsonValue(Object jsonObject) throws JSONException {
		return MockMvcResultMatchers.content().json(getStandardJsonReturnString(jsonObject));
	}
	
	private static String getStandardJsonReturnString(Object jsonObject) throws JSONException {
		
		JSONObject json = new JSONObject();
		json.put("resultCode", 0);
		json.put("resultErrorMessage", JSONObject.NULL);
		if (jsonObject != null) {
			if (jsonObject instanceof String)
				json.put("resultValues", new JSONObject((String)jsonObject));
			else
				json.put("resultValues", jsonObject);
		} else {
			json.put("resultValues", JSONObject.NULL);
		}
		return json.toString();
	}
}
