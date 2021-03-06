package com.duoshouji.restapi.end2endtest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.duoshouji.restapi.LoginMethod;
import com.duoshouji.service.util.MobileNumber;
import com.duoshouji.service.util.VerificationCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	public CommonDistrict emitCommonDistrict() {
		return new CommonDistrict();
	}
	
	public CommonTag emitCommonTag(long categoryId, long brandId) {
		return new CommonTag(categoryId, brandId);
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
		protected MockHttpServletRequestBuilder getBuilder() throws JsonProcessingException {
			return post("/message/verification-code").contentType(MediaType.APPLICATION_JSON).content(
					new ObjectMapper().writeValueAsString(Collections.singletonMap("mobile", mobile.toString())));
		}
	}
	
	public class VerificationCodeLogin extends DynamicResourceRequest {
		private MobileNumber mobile;
		private VerificationCode code;

		private VerificationCodeLogin(MobileNumber mobile, VerificationCode code) {
			super();
			this.mobile = mobile;
			this.code = code;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws JsonProcessingException {
			Map<String, Object> requestData = new HashMap<String, Object>();
			requestData.put("loginMethod", LoginMethod.VERIFICATION_CODE.name());
			requestData.put("mobileNumber", mobile.toString());
			requestData.put("secret", code.toString());
	    	return post("/user/login").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(requestData));
		}
	}
	
	public class CredentialLogin extends DynamicResourceRequest {
		private MobileNumber mobile;
		private String password;
		
		private CredentialLogin(MobileNumber mobile, String password) {
			super();
			this.mobile = mobile;
			this.password = password;
		}

		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws JsonProcessingException {
			Map<String, Object> requestData = new HashMap<String, Object>();
			requestData.put("loginMethod", LoginMethod.CREDENTIAL.name());
			requestData.put("mobileNumber", mobile.toString());
			requestData.put("secret", password);
	    	return post("/user/login").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(requestData));
		}
	}
	
	public class CommonTag extends DynamicResourceRequest {
		private long categoryId;
		private long brandId;
		
		private CommonTag(long categoryId, long brandId) {
			super();
			this.categoryId = categoryId;
			this.brandId = brandId;
		}
		
		@Override
		protected MockHttpServletRequestBuilder getBuilder() throws Exception {
			return get("/common/tags")
					.param("categoryId", Long.toString(categoryId))
					.param("brandId", Long.toString(brandId));
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
