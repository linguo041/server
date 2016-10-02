package com.duoshouji.server.service.image;

import javax.servlet.ServletRequest;

import com.duoshouji.util.Image;

public interface ImageUploadCallback {

	void fireImageUpload(ServletRequest originalUploadRequest, Image imageInfo) throws StoreImageException;
	
	void fireImageUpload(ServletRequest originalUploadRequest, Image[] imageInfos) throws StoreImageException;
}
