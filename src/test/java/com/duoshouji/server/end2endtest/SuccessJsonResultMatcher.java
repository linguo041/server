package com.duoshouji.server.end2endtest;

import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.test.web.servlet.MvcResult;

public class SuccessJsonResultMatcher implements ResultMatcher {

	@Override
	public void match(MvcResult result) throws Exception {
		JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		verifyResultCode(json);
		verifyJsonResult(json);
	}
	
	private void verifyResultCode(JSONObject json) throws Exception {
		Assert.assertEquals(0, json.getInt("resultCode"));
		Assert.assertTrue(json.isNull("resultErrorMessage"));
	}
	
	protected void verifyJsonResult(JSONObject json) throws Exception {
	}

}
