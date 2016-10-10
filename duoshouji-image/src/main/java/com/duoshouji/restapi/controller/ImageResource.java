package com.duoshouji.restapi.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.duoshouji.core.ImageStore;
import com.duoshouji.core.ImageUploadCallback;
import com.duoshouji.core.StoreImageException;
import com.duoshouji.service.util.Image;

@RestController
public class ImageResource {

	private ImageUploadCallback callback;
	private ImageStore imageStore;
	
	@Autowired
	public ImageResource(ImageUploadCallback callback, ImageStore imageStore) {
		super();
		this.callback = callback;
		this.imageStore = imageStore;
	}	

	@PostMapping("/users/{user-id}/settings/personal-information/protrait")
	public void uploadUserPortrait(
			@RequestParam("image") MultipartFile imageFile,
			@PathVariable("user-id") long userId,
			ServletRequest webRequest) throws IOException, StoreImageException {
		if (!imageFile.isEmpty()) {
			final Image image = imageStore.saveUserPortrait(userId, imageFile.getInputStream());
			callback.fireImageUpload(webRequest, image);
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
		callback.fireImageUpload(webRequest, imageStore.saveNoteImage(noteId, imageStreams));
	}
}
