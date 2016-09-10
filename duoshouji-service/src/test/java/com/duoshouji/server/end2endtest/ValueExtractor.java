package com.duoshouji.server.end2endtest;

import org.json.JSONObject;
import org.springframework.test.web.servlet.MvcResult;


public abstract class ValueExtractor<T> {

	public static final ValueExtractor<String> TOKEN_EXTRACTOR =
			new ValueExtractor<String>() {
				@Override
				public String extractFrom(MvcResult result) throws Exception {
					JSONObject loginResult = new JSONObject(result.getResponse().getContentAsString());
					return loginResult.getJSONObject("resultValues").getString("token");
				}
			};
	
	public static final ValueExtractor<Long> NOTE_ID_EXTRACTOR =
			new ValueExtractor<Long>() {
				@Override
				public Long extractFrom(MvcResult result) throws Exception {
					JSONObject loginResult = new JSONObject(result.getResponse().getContentAsString());
					return loginResult.getJSONObject("resultValues").getLong("noteId");
				}
			};	
			
	public abstract T extractFrom(MvcResult result) throws Exception;
}
