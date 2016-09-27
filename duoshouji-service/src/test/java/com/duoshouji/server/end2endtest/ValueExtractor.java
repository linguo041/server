package com.duoshouji.server.end2endtest;

import org.json.JSONArray;
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
