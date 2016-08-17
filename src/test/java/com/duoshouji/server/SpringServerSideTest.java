package com.duoshouji.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.duoshouji.server.session.TokenManager;
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
    
	@Test
	public void loginByUserNameAndPassword() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = post("/login/authenticate/credential");
		requestBuilder.param("mobile", MockConstants.MOCK_USER_IDENTIFIER.toString());
		requestBuilder.param("password", MockConstants.MOCK_PASSWORD.toString());
		mockMvc.perform(requestBuilder)
			.andExpect(statusIsOk())
			.andExpect(headerContainsTokenForMockUser())
			.andExpect(withJsonValue("{\"loginResultCode\" : 0}"));
	}
	
	@Test
	public void loginByVerificationCode() throws Exception {
		MockHttpServletRequestBuilder requestBuilder;
		requestBuilder = post("/message/verification-code/login").param("mobile", MockConstants.MOCK_USER_IDENTIFIER.toString());
		mockMvc.perform(requestBuilder)
			.andExpect(statusIsOk())
			.andExpect(withJsonValue(null));
		
		requestBuilder = post("/login/authenticate/verification-code");
		requestBuilder.param("mobile", MockConstants.MOCK_USER_IDENTIFIER.toString());
		requestBuilder.param("code", getMessageSender().findHistory(MockConstants.MOCK_USER_IDENTIFIER).toString());
		mockMvc.perform(requestBuilder)
			.andExpect(statusIsOk())
			.andExpect(headerContainsTokenForMockUser())
			.andExpect(withJsonValue("{\"loginSuccess\" : true}"));
	}
	
	@Test
	public void setPassword() throws Exception {
		final String token = loginWithMockUser();
		mockMvc.perform(post("/message/verification-code").param("purpose", "CHANGE_PASSWORD").header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token));
		
		mockMvc.perform(post("/account/" + MockConstants.MOCK_USER_IDENTIFIER + "/settings/security/password")
			.param("password", "newpassword")
			.param("code", getMessageSender().findHistory(MockConstants.MOCK_USER_IDENTIFIER).toString())
			.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token))
			.andExpect(statusIsOk());
	}
	
	@Test
	public void getSquareNotes() throws Exception {
		final String token = loginWithMockUser();
		
		JSONArray array = new JSONArray();
		JSONObject json = new JSONObject();
		json.put("noteId", MockConstants.MOCK_NOTE_ID);
		json.put("title", MockConstants.MOCK_NOTE_TITLE);
		json.put("image", MockConstants.MOCK_NOTE_MAIN_IMAGE.getURL());
		json.put("imageWidth", MockConstants.MOCK_NOTE_MAIN_IMAGE.getWidth());
		json.put("imageHeight", MockConstants.MOCK_NOTE_MAIN_IMAGE.getHeight());
		json.put("portrait", MockConstants.MOCK_USER_PORTRAIT.getURL());
		json.put("rank", 0);
		json.put("likeCount", 0);
		json.put("commentCount", 0);
		array.put(json);
		
		mockMvc.perform(post("/notes")
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token))
				.andExpect(statusIsOk())
				.andExpect(withJsonValue(array));
	}
	
	private String loginWithMockUser() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = post("/login/authenticate/credential");
		requestBuilder.param("mobile", MockConstants.MOCK_USER_IDENTIFIER.toString());
		requestBuilder.param("password", MockConstants.MOCK_PASSWORD.toString());
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		return result.getResponse().getHeader(Constants.APP_TOKEN_HTTP_HEADER_NAME);
	}
	
	private MockMessageSender getMessageSender() {
		return (MockMessageSender) wac.getBean(MessageProxyFactory.class);
	}
	
	private MockTokenManager getTokenManager() {
		return (MockTokenManager) wac.getBean(TokenManager.class);
	}
	
	private ResultMatcher headerContainsTokenForMockUser() {
		return MockMvcResultMatchers.header().string(Constants.APP_TOKEN_HTTP_HEADER_NAME, getTokenManager().findToken(MockConstants.MOCK_USER_IDENTIFIER.toString()));
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
		if (jsonObject != null) {
			if (jsonObject instanceof String)
				json.put("resultValue", new JSONObject((String)jsonObject));
			else
				json.put("resultValue", jsonObject);
		}
		return json.toString();
	}
}
