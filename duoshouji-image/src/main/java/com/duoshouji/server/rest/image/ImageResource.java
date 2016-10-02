package com.duoshouji.server.rest.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.servlet.ServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.duoshouji.server.service.image.ImageStore;
import com.duoshouji.server.service.image.ImageUploadCallback;
import com.duoshouji.server.service.image.StoreImageException;
import com.duoshouji.util.Image;
import com.duoshouji.util.MobileNumber;

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

	@RequestMapping(path = "/accounts/{account-id}/settings/profile/protrait", method = RequestMethod.POST)
	public void uploadUserPortrait(
			@RequestParam("image") MultipartFile image,
			@PathVariable("account-id") MobileNumber userId,
			ServletRequest webRequest) throws IOException, StoreImageException {
		if (!image.isEmpty()) {
			final byte[] imageBytes = IOUtils.toByteArray(image.getInputStream());
			BufferedImage swingImage = toSwingImage(new ByteArrayInputStream(imageBytes));
			URL imageUrl = imageStore.saveUserPortrait(userId, new ByteArrayInputStream(imageBytes));
			callback.fireImageUpload(webRequest, new Image(swingImage.getWidth(), swingImage.getHeight(), imageUrl.toString()));
		}
	}
	
	@RequestMapping(path = "/notes/{note-id}/images", method = RequestMethod.POST)
	public void uploadNoteImage(
			@RequestParam("images") MultipartFile[] imageFiles,
			@PathVariable("note-id") long noteId,
			ServletRequest webRequest) throws IOException, StoreImageException {
		Image[] images = new Image[imageFiles.length];
		int index = 0;
		for (MultipartFile imageFile : imageFiles) {
			final byte[] imageBytes = IOUtils.toByteArray(imageFile.getInputStream());
			BufferedImage swingImage = toSwingImage(new ByteArrayInputStream(imageBytes));
			URL imageUrl = imageStore.saveNoteImage(noteId, index, new ByteArrayInputStream(imageBytes));
			images[index] = new Image(swingImage.getWidth(), swingImage.getHeight(), imageUrl.toString());
			++index;
		}
		callback.fireImageUpload(webRequest, images);
	}
	
	private BufferedImage toSwingImage(InputStream imageInput) throws IOException {
		final BufferedImage swingImage = ImageIO.read(imageInput);
		imageInput.close();
		return swingImage;
	}
}
