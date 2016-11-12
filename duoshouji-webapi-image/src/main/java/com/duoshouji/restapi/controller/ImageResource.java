package com.duoshouji.restapi.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import javax.servlet.ServletRequest;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.core.ImageStore;
import com.duoshouji.core.ImageUploadCallback;
import com.duoshouji.core.StoreImageException;
import com.duoshouji.restapi.AuthenticationAdvice;
import com.duoshouji.restapi.controller.model.request.UploadUserPortraitRequestData;
import com.duoshouji.service.util.Image;

@RestController
public class ImageResource extends AuthenticationAdvice {

	private ImageUploadCallback callback;
	private ImageStore imageStore;

	@Required
	@Autowired
	public void setCallback(ImageUploadCallback callback) {
		this.callback = callback;
	}

	@Required
	@Autowired
	public void setImageStore(ImageStore imageStore) {
		this.imageStore = imageStore;
	}

	@PostMapping("/user/settings/personal-information/portrait")
	public void uploadUserPortrait(
			@RequestBody UploadUserPortraitRequestData requestData,
			@ModelAttribute("userId") long userId,
			ServletRequest webRequest) throws IOException, StoreImageException {
			final Image image = imageStore.saveUserPortrait(userId, decodeImageData(requestData.image));
			callback.firePortraitUpload(webRequest, image);
	}
	
	@PostMapping("/notes/{note-id}/images")
	public void uploadNoteImage(
			@RequestBody String requestData,
			@PathVariable("note-id") long noteId,
			ServletRequest webRequest) throws IOException, StoreImageException {
		LogManager.getLogger().info(requestData);
	}
	
	private InputStream decodeImageData(String imageData) {
		final int prefixIndex = imageData.indexOf(',');
		if (prefixIndex >= 0) {
			imageData = imageData.substring(prefixIndex + 1);
		}
		return new ByteArrayInputStream(Base64.getDecoder().decode(imageData));
	}
}
