package com.duoshouji.service.util;


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

	@Override
	public int hashCode() {
		return url.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Image))
			return false;
		Image that = (Image) obj;
		return url.equals(that.url) && width == that.width && height == that.height;
	}
}
