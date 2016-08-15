package com.duoshouji.server.util;

import java.net.URL;

public class Image {
	private int width;
	private int height;
	private URL url;
	
	public Image(int width, int height, URL url) {
		super();
		this.width = width;
		this.height = height;
		this.url = url;
	}

	public URL getURL() {
		return url;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
