package com.duoshouji.restapi.controller.model.request;

import com.duoshouji.restapi.image.ImageMark;

public class UploadNoteImageRequestData {

	public ImageInfo[] images;
	
	public static class ImageInfo {
		public String image;
		public ImageMark[] marks;
	}
}
