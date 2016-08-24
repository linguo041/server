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
    	requestBuilder = post("/message/verification-code/login").param("mobile", MockConstants.MOCK_MOBILE_NUMBER.toString());
    	mockMvc.perform(requestBuilder)
    	.andExpect(statusIsOk())
    	.andExpect(withJsonValue(null));
    	
    	requestBuilder = post("/login/authenticate/verification-code");
    	requestBuilder.param("mobile", MockConstants.MOCK_MOBILE_NUMBER.toString());
    	requestBuilder.param("code", getLastVerificationCodeForMockUser());
    	return getTokenFromLoginResult(mockMvc.perform(requestBuilder).andReturn());
    }
    
    public void setPassword(String userToken) throws Exception {    	
    	mockMvc.perform(post("/message/verification-code/reset-password")
    			.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, userToken));
     	mockMvc.perform(
       			post("/accounts/"+MockConstants.MOCK_MOBILE_NUMBER.toString()+"/settings/security/password")
    			.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, userToken)
    			.param("code", getLastVerificationCodeForMockUser())
    			.param("password", MockConstants.MOCK_PASSWORD.toString())
    	)
    	.andExpect(statusIsOk())
    	.andExpect(withJsonValue("{\"passwordUpdateResultCode\" : 0}"));
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
				post("/login/authenticate/credential")
				.param("mobile", MockConstants.MOCK_MOBILE_NUMBER.toString())
				.param("password", MockConstants.MOCK_PASSWORD.toString())				
			).andReturn()
		);
	}
	
	public void setNickname(String userToken) throws Exception {
		mockMvc.perform(
				post("/accounts/"+MockConstants.MOCK_MOBILE_NUMBER.toString()+"/settings/profile")
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, userToken)
				.param("nickname", MockConstants.MOCK_NICKNAME)				
			).andExpect(statusIsOk());
	}
	
	public void uploadUserPortrait(String userToken) throws Exception {		
		mockMvc.perform(
				fileUpload("/accounts/${account-id}/settings/profile/protrait", MockConstants.MOCK_MOBILE_NUMBER.toString())
				.file(new MockMultipartFile("image", getImageBytes("portrait.gif")))
			);
	}
	
	public void addNotes(int noteCount, String userToken) throws Exception {
		for (int i = 0; i < noteCount; ++i) {
			long noteId = getNoteIdFromReturnValue(mockMvc.perform(post("/accounts/"+MockConstants.MOCK_MOBILE_NUMBER+"/notes/note")
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, userToken)
				.param("title", "title" + this.noteCount)
				.param("content", "content" + this.noteCount)
			).andReturn());
	
			mockMvc.perform(
				fileUpload("/notes/${note-id}/images/main-image", noteId)
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
	
	private JSONArray buildSquareNoteReturn(int... noteIds) throws JSONException {
		JSONArray array = new JSONArray();
		for (int noteId : noteIds) {
			JSONObject json = new JSONObject();
			json.put("noteId", noteId);
			json.put("title", MockConstants.MOCK_NOTE_TITLE);
			json.put("image", MockConstants.MOCK_NOTE_MAIN_IMAGE.getUrl());
			json.put("imageWidth", MockConstants.MOCK_NOTE_MAIN_IMAGE.getWidth());
			json.put("imageHeight", MockConstants.MOCK_NOTE_MAIN_IMAGE.getHeight());
			json.put("portrait", MockConstants.MOCK_USER_PORTRAIT.getUrl());
			json.put("rank", 0);
			json.put("likeCount", 0);
			json.put("commentCount", 0);
			array.put(json);
		}
		return array;
	}
	
	public void getSquareNotes(String userToken) throws Exception {
		
		mockMvc.perform(get("/notes")
				.param("loadedSize", Integer.toString(0))
				.param("pageSize", Integer.toString(2))
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, userToken))
				.andExpect(statusIsOk())
				.andExpect(withJsonValue(buildSquareNoteReturn()));
		
		mockMvc.perform(get("/notes")
				.param("loadedSize", Integer.toString(-1))
				.param("pageSize", Integer.toString(2))
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, userToken))
				.andExpect(statusIsOk())
				.andExpect(withJsonValue(buildSquareNoteReturn(0,1)));
		
		mockMvc.perform(get("/notes")
				.param("loadedSize", Integer.toString(2))
				.param("pageSize", Integer.toString(2))
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, userToken))
				.andExpect(statusIsOk())
				.andExpect(withJsonValue(buildSquareNoteReturn(2)));
	}
	
	@Test
	public void duoShouJiEnd2EndTest() throws Exception {
		String userToken = firstLogin();
		setPassword(userToken);
		logout(userToken);
		userToken = credentialLogin();
		setNickname(userToken);
		uploadUserPortrait(userToken);
		addNotes(1, userToken);
		addNotes(2, userToken);
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
