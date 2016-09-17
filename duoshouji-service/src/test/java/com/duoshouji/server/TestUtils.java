package com.duoshouji.server;

import java.io.IOException;
import java.io.InputStream;

public abstract class TestUtils {
	
	private TestUtils(){}
	
	public static InputStream getImageBytes(String imageName) throws IOException {
		return TestUtils.class.getClassLoader().getResourceAsStream(imageName);
	}


}
