package com.duoshouji.restapi.image;

import com.duoshouji.service.util.Image;


public class ImageJsonAdapter {
	public String url;
	public int width;
	public int height;
	
	public ImageJsonAdapter(){}
	
	public ImageJsonAdapter(Image image) {
		this.url = image.getUrl();
		this.width = image.getWidth();
		this.height = image.getHeight();
	}

}
