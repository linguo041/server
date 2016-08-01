package com.duoshouji.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/hello")
public class Hello {
	
	@RequestMapping(value="/world", method=RequestMethod.GET)
	public @ResponseBody String hello() {
		return "hello world";
	}
}
