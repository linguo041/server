package com.duoshouji.restapi.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import javax.servlet.ServletRequest;

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
import com.duoshouji.restapi.controller.model.request.UploadNoteImageRequestData;
import com.duoshouji.restapi.controller.model.request.UploadUserPortraitRequestData;
import com.duoshouji.restapi.image.ImageJsonAdapter;
import com.duoshouji.restapi.image.UploadNoteImageCallbackData;
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
			@RequestBody UploadNoteImageRequestData requestData,
			@PathVariable("note-id") long noteId,
			ServletRequest webRequest) throws IOException, StoreImageException {
		InputStream[] imageStreams = new InputStream[requestData.images.length];
		for (int i = 0; i < imageStreams.length; ++i) {
			imageStreams[i] = decodeImageData(requestData.images[i].image);
		}
		Image[] images = imageStore.saveNoteImage(noteId, imageStreams);
		UploadNoteImageCallbackData[] callbackData = new UploadNoteImageCallbackData[images.length];
		for (int i = 0; i < callbackData.length; ++i) {
			callbackData[i] = new UploadNoteImageCallbackData();
			callbackData[i].imageInfo = new ImageJsonAdapter(images[i]);
			callbackData[i].imageMarks = requestData.images[i].marks;
		}
		callback.fireNoteImageUpload(webRequest, callbackData);
	}
	
	private InputStream decodeImageData(String imageData) {
		return new ByteArrayInputStream(Base64.getDecoder().decode(imageData));
	}
}
