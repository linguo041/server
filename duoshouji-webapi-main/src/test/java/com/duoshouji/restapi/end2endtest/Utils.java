package com.duoshouji.restapi.end2endtest;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

class Utils {

	static String getJsonString(Map<String, Object> pairs) throws Exception {
		return new ObjectMapper().writeValueAsString(pairs);
	}
}
