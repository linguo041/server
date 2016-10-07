package com.duoshouji.server.end2endtest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.test.web.servlet.MvcResult;


public abstract class ValueExtractor<T> {

	public static final ValueExtractor<LoginResult> LONIN_RESULT_EXTRACTOR =
			new ValueExtractor<LoginResult>() {
				@Override
				public LoginResult extractFrom(MvcResult result) throws Exception {
					JSONObject loginResult = new JSONObject(result.getResponse().getContentAsString());
					LoginResult returnValue = new LoginResult();
					returnValue.token = loginResult.getJSONObject("resultValues").getString("token");
					returnValue.userId = loginResult.getJSONObject("resultValues").getLong("userId");
					return returnValue;
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
	
	public static final ValueExtractor<long[]> IDS_EXTRACTOR =
			new ValueExtractor<long[]>() {

				@Override
				public long[] extractFrom(MvcResult result) throws Exception {
					JSONObject j = new JSONObject(result.getResponse().getContentAsString());
					JSONArray a = j.getJSONArray("resultValues");
					long[] tagIds = new long[a.length()];
					for (int i = 0; i < a.length(); ++i) {
						tagIds[i] = a.getJSONObject(i).getLong("identifier");
					}
					return tagIds;
				}
		
	};
			
	public abstract T extractFrom(MvcResult result) throws Exception;
}
