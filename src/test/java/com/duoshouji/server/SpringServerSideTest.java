package com.duoshouji.server;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
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
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.header().string(Constants.APP_TOKEN_HTTP_HEADER_NAME, ((MockTokenManager)wac.getBean(TokenManager.class)).findToken(MockConstants.MOCK_USER_IDENTIFIER.toString())));
	}
	
	@Test
	public void loginByVerificationCode() throws Exception {
		MockHttpServletRequestBuilder requestBuilder;
		requestBuilder = post("/message/verification-code");
		requestBuilder.param("mobile", MockConstants.MOCK_USER_IDENTIFIER.toString());
		requestBuilder.param("purpose", "LOGIN");
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
		
		requestBuilder = post("/login/authenticate/verification-code");
		requestBuilder.param("mobile", MockConstants.MOCK_USER_IDENTIFIER.toString());
		requestBuilder.param("code", ((MockMessageSender)wac.getBean(MessageProxyFactory.class)).findHistory(MockConstants.MOCK_USER_IDENTIFIER).toString());
		mockMvc.perform(requestBuilder)
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.header().string(Constants.APP_TOKEN_HTTP_HEADER_NAME, ((MockTokenManager)wac.getBean(TokenManager.class)).findToken(MockConstants.MOCK_USER_IDENTIFIER.toString())));
	}
	
	@Test
	public void getSquareNotes() throws Exception {
		MockTokenManager tokenManager = (MockTokenManager) wac.getBean(TokenManager.class);
		
		mockMvc.perform(post("/login/authenticate/credential")
				.param("mobile", MockConstants.MOCK_USER_IDENTIFIER.toString())
				.param("password", MockConstants.MOCK_PASSWORD.toString()));
		
		mockMvc.perform(post("/notes")
				.header(Constants.APP_TOKEN_HTTP_HEADER_NAME, tokenManager.findToken(MockConstants.MOCK_USER_IDENTIFIER.toString())))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().json(
						new JSONObject().put("resultCode", 0).put("resultValue", new JSONArray()).toString()
						));
	}
}
