package com.duoshouji.restapi.controller.model.response;

import com.duoshouji.service.util.Image;

public class ImageJsonAdapter {

	private Image image;
	
	public ImageJsonAdapter(Image image) {
		this.image = image;
	}

	public String getImageUrl() {
		return image.getUrl();
	}
	
	public int getImageWidth() {
		return image.getWidth();
	}
	
	public int getImageHeight() {
		return image.getHeight();
	}
}
