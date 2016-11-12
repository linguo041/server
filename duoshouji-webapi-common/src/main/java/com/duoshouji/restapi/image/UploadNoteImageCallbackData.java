package com.duoshouji.restapi.image;

public class UploadNoteImageCallbackData {

	public ImageInfo[] images;
	
	public static class ImageInfo {
		public ImageJsonAdapter imageInfo;
		public ImageMark[] imageMarks;		
	}
}
