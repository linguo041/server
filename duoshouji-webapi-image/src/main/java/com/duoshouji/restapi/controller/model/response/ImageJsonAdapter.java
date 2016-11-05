package com.duoshouji.restapi.controller.model.response;

import com.duoshouji.service.util.Image;

public class ImageJsonAdapter {

	private Image image;
	
	public ImageJsonAdapter(Image image) {
		this.image = image;
	}

	public String getUrl() {
		return image.getUrl();
	}
	
	public int getWidth() {
		return image.getWidth();
	}
	
	public int getHeight() {
		return image.getHeight();
	}
}
