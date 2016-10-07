package com.duoshouji.server.rest.image;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.duoshouji.server.service.image.ImageStore;
import com.duoshouji.server.service.image.ImageUploadCallback;
import com.duoshouji.server.service.image.StoreImageException;
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

	@PutMapping(path = "/users/{user-id}/settings/personal-information/protrait")
	public void uploadUserPortrait(
			@RequestParam("image") MultipartFile imageFile,
			@PathVariable("user-id") long userId,
			ServletRequest webRequest) throws IOException, StoreImageException {
		if (!imageFile.isEmpty()) {
			final Image image = imageStore.saveUserPortrait(userId, imageFile.getInputStream());
			callback.fireImageUpload(webRequest, image);
		}
	}
	
	@RequestMapping(path = "/notes/{note-id}/images", method = RequestMethod.POST)
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
