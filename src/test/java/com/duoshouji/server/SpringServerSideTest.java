package com.duoshouji.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.duoshouji.server.service.dao.UserNoteDao;
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
			.andExpect(withJsonValue("{\"loginResultCode\" : 0, \"token\" : \""+ getTokenForMockUser() +"\"}"));
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
			.andExpect(withJsonValue("{\"loginSuccess\" : true, \"token\" : \""+ getTokenForMockUser() +"\"}"));
	}
	
	private JSONArray buildSquareNoteReturn(int... noteIds) throws JSONException {
		JSONArray array = new JSONArray();
		for (int noteId : noteIds) {
			JSONObject json = new JSONObject();
			json.put("noteId", noteId);
			json.put("title", MockConstants.MOCK_NOTE_TITLE);
			json.put("image", MockConstants.MOCK_NOTE_MAIN_IMAGE.getURL());
			json.put("imageWidth", MockConstants.MOCK_NOTE_MAIN_IMAGE.getWidth());
			json.put("imageHeight", MockConstants.MOCK_NOTE_MAIN_IMAGE.getHeight());
			json.put("portrait", MockConstants.MOCK_USER_PORTRAIT.getURL());
			json.put("rank", 0);
			json.put("likeCount", 0);
			json.put("commentCount", 0);
			array.put(json);
		}
		return array;
	}
	
	@Test
	public void getSquareNotes() throws Exception {
		final String token = loginWithMockUser();
		
		mockMvc.perform(get("/notes")
				.param("loadedSize", Integer.toString(0))
				.param("pageSize", Integer.toString(2))
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token))
				.andExpect(statusIsOk())
				.andExpect(withJsonValue(buildSquareNoteReturn()));
		
		getDao().addNoteForMockUser();
		getDao().addNoteForMockUser();
		getDao().addNoteForMockUser();
		
		mockMvc.perform(get("/notes")
				.param("loadedSize", Integer.toString(-1))
				.param("pageSize", Integer.toString(2))
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token))
				.andExpect(statusIsOk())
				.andExpect(withJsonValue(buildSquareNoteReturn(0,1)));
		
		mockMvc.perform(get("/notes")
				.param("loadedSize", Integer.toString(2))
				.param("pageSize", Integer.toString(2))
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, token))
				.andExpect(statusIsOk())
				.andExpect(withJsonValue(buildSquareNoteReturn(2)));
	}
	
	private String loginWithMockUser() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = post("/login/authenticate/credential");
		requestBuilder.param("mobile", MockConstants.MOCK_USER_IDENTIFIER.toString());
		requestBuilder.param("password", MockConstants.MOCK_PASSWORD.toString());
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		JSONObject loginResult = new JSONObject(result.getResponse().getContentAsString());
		loginResult = loginResult.getJSONObject("resultValues");
		return loginResult.getString("token");
	}
	
	private MockMessageSender getMessageSender() {
		return (MockMessageSender) wac.getBean(MessageProxyFactory.class);
	}
	
	private MockTokenManager getTokenManager() {
		return (MockTokenManager) wac.getBean(TokenManager.class);
	}
	
	private MockMysqlDao getDao() {
		return (MockMysqlDao) wac.getBean(UserNoteDao.class);
	}
	
	private String getTokenForMockUser() {
		return getTokenManager().findToken(MockConstants.MOCK_USER_IDENTIFIER.toString());
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
