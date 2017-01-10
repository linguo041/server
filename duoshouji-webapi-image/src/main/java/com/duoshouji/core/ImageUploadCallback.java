package com.duoshouji.core;

import javax.servlet.ServletRequest;

import com.duoshouji.restapi.image.UploadNoteImageCallbackData;
import com.duoshouji.service.util.Image;

public interface ImageUploadCallback {

	void firePortraitUpload(ServletRequest originalUploadRequest, Image imageInfo) throws StoreImageException;
	
	void fireNoteImageUpload(ServletRequest originalUploadRequest, UploadNoteImageCallbackData callbackData) throws StoreImageException;
}
