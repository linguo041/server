package com.duoshouji.server.end2endtest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.VerificationCode;

public class MockClient {
	
	protected final MockMvc mockMvc;
	
	public MockClient(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}
	
	public SendLoginVerificationCode emitSendLoginVerificationCode(MobileNumber mobile) {
		return new SendLoginVerificationCode(mobile);
	}
	
	public VerificationCodeLogin emitVerificationCodeLogin(MobileNumber mobile, VerificationCode code) {
		return new VerificationCodeLogin(mobile, code);
	}
	
	public CredentialLogin emitCredentialLogin(MobileNumber mobile, String password) {
		return new CredentialLogin(mobile, password);
	}
	
	public CommonCategory emitCommonCategory() {
		return new CommonCategory();
	}
	
	public CommonBrand emitCommonBrand() {
		return new CommonBrand();
	}
	
	public CommonProduct emitCommonProduct() {
		return new CommonProduct();
	}
	
	public CommonDistrict emitCommonDistrict() {
		return new CommonDistrict();
	}
	
	public CommonTag emitCommonTag() {
		return new CommonTag();
	}
	
	public CommonChannel emitCommonChannel() {
		return new CommonChannel();
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
		
		public final <T> T extract(ValueExtractor<T> extractor) throws Exception {
			return extractor.extractFrom(result);
		}
		
		protected final void doPerform() throws Exception {
			result = mockMvc.perform(getBuilder())
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andReturn();
		}
		
		protected abstract MockHttpServletRequestBuilder getBuilder() throws Exception;
	}

	private static final SuccessJsonResultMatcher DEFAULT_SUCCESS_JSON_RESULT_MATCHER = new SuccessJsonResultMatcher();
	
	public abstract class DynamicResourceRequest extends ClientRequest {
		
		public ClientRequest performAndExpectSuccess() throws Exception {
			doPerform();
			expect(getDefaultSuccessMatcher());
			return this;
		}
		
		protected SuccessJsonResultMatcher getDefaultSuccessMatcher() {
			return DEFAULT_SUCCESS_JSON_RESULT_MATCHER;
		}
	}
	
	public class SendLoginVerificationCode extends DynamicResourceRequest {
		private MobileNumber mobile;
		
		private SendLoginVerificationCode(MobileNumber mobile) {
			super();
			this.mobile = mobile;
		}
		@Override
		protected MockHttpServletRequestBuilder getBuilder() {
			return post("/accounts/{account-id}/message/verification-code/login", mobile);
		}
	}
	
	private static final SuccessJsonResultMatcher VERIFICATION_CODE_LOGIN_RESULT_MATCHER =
			new SuccessJsonResultMatcher() {

				@Override
				protected void verifyJsonResult(JSONObject json)
						throws Exception {
					Assert.assertTrue(json.getJSONObject("resultValues").getBoolean("loginSuccess"));
				}
		
			};
	
	public class VerificationCodeLogin extends DynamicResourceRequest {
		private MobileNumber mobile;
		private VerificationCode code;

		private VerificationCodeLogin(MobileNumber mobile, VerificationCode code) {
			super();
			this.mobile = mobile;
			this.code = code;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() {
	    	return post("/accounts/{account-id}/login/verification-code", mobile).param("code", code.toString());
		}

		@Override
		protected SuccessJsonResultMatcher getDefaultSuccessMatcher() {
			return VERIFICATION_CODE_LOGIN_RESULT_MATCHER;
		}
	}
	
	private static final SuccessJsonResultMatcher CREDENTIAL_LOGIN_RESULT_MATCHER =
			new SuccessJsonResultMatcher() {

				@Override
				protected void verifyJsonResult(JSONObject json)
						throws Exception {
					Assert.assertEquals(0, json.getJSONObject("resultValues").getInt("loginResultCode"));
				}
		
			};
	
	public class CredentialLogin extends DynamicResourceRequest {
		private MobileNumber mobile;
		private String password;
		
		private CredentialLogin(MobileNumber mobile, String password) {
			super();
			this.mobile = mobile;
			this.password = password;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() {
	    	return post("/accounts/{account-id}/login/credential", mobile)
	    			.param("password", password);
		}
		
		@Override
		protected SuccessJsonResultMatcher getDefaultSuccessMatcher() {
			return CREDENTIAL_LOGIN_RESULT_MATCHER;
		}
	}
	public class CommonTag extends DynamicResourceRequest {

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return get("/common/tags");
		}
		
	}
	
	public class CommonCategory extends DynamicResourceRequest {

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return get("/common/commodity/categories");
		}
		
	}
	
	public class CommonBrand extends DynamicResourceRequest {

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return get("/common/commodity/brands");
		}
		
	}
	
	public class CommonProduct extends DynamicResourceRequest {

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return get("/common/commodity/products");
		}
		
	}
	
	public class CommonDistrict extends DynamicResourceRequest {

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return get("/common/geography/cities");
		}
	}
	
	public class CommonChannel extends DynamicResourceRequest {

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return get("/common/channels");
		}
		
	}
}
