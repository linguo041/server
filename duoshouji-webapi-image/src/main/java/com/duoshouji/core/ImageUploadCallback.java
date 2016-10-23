package com.duoshouji.core;

import javax.servlet.ServletRequest;

import com.duoshouji.service.util.Image;

public interface ImageUploadCallback {

	void firePortraitUpload(ServletRequest originalUploadRequest, Image imageInfo) throws StoreImageException;
	
	void fireNoteImageUpload(ServletRequest originalUploadRequest, Image[] imageInfos) throws StoreImageException;
}
