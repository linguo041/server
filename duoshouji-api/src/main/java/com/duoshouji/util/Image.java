package com.duoshouji.util;


public class Image {
	private int width;
	private int height;
	private String url;
	
	public Image(int width, int height, String url) {
		super();
		this.width = width;
		this.height = height;
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
