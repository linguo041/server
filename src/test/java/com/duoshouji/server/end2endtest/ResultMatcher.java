package com.duoshouji.server.end2endtest;

import org.springframework.test.web.servlet.MvcResult;

public interface ResultMatcher {
	
	void match(MvcResult result) throws Exception;
}