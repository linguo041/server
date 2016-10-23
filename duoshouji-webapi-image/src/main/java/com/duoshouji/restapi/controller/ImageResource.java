package com.duoshouji.restapi.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.duoshouji.core.ImageStore;
import com.duoshouji.core.ImageUploadCallback;
import com.duoshouji.core.StoreImageException;
import com.duoshouji.restapi.AuthenticationAdvice;
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
			@RequestParam("image") MultipartFile imageFile,
			@ModelAttribute("userId") long userId,
			ServletRequest webRequest) throws IOException, StoreImageException {
		if (!imageFile.isEmpty()) {
			final Image image = imageStore.saveUserPortrait(userId, imageFile.getInputStream());
			callback.firePortraitUpload(webRequest, image);
		}
	}
	
	@PostMapping("/notes/{note-id}/images")
	public void uploadNoteImage(
			@RequestParam("images") MultipartFile[] imageFiles,
			@PathVariable("note-id") long noteId,
			ServletRequest webRequest) throws IOException, StoreImageException {
		InputStream[] imageStreams = new InputStream[imageFiles.length];
		for (int i = 0; i < imageFiles.length; ++i) {
			imageStreams[i] = imageFiles[i].getInputStream();
		}
		callback.fireNoteImageUpload(webRequest, imageStore.saveNoteImage(noteId, imageStreams));
	}
}
