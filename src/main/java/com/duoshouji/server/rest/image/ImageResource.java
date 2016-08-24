package com.duoshouji.server.rest.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.duoshouji.server.service.dao.UserNoteDao;
import com.duoshouji.server.service.image.ImageStore;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;

@RestController
public class ImageResource {

	private UserNoteDao dao;
	private ImageStore imageStore;
	
	@Autowired
	public ImageResource(UserNoteDao dao, ImageStore imageStore) {
		super();
		this.dao = dao;
		this.imageStore = imageStore;
	}

	@RequestMapping(path = "/accounts/${account-id}/settings/profile/protrait", method = RequestMethod.POST)
	public void uploadPortrait(
			@RequestParam("image") MultipartFile image,
			@PathVariable("account-id") String accountId) throws IOException {
		if (!image.isEmpty()) {
			BufferedImage swingImage = toSwingImage(image);
			String imageUrl = imageStore.savePortrait(accountId, swingImage);
			dao.savePortrait(new MobileNumber(accountId), new Image(swingImage.getWidth(), swingImage.getHeight(), imageUrl));
		}
	}
	
	@RequestMapping(path = "/notes/${note-id}/images/main-image", method = RequestMethod.POST)
	public void uploadNoteImage(
			@RequestParam("image") MultipartFile image,
			@PathVariable("note-id") long noteId
			) throws IOException {
		if (!image.isEmpty()) {
			BufferedImage swingImage = toSwingImage(image);
			String imageUrl = imageStore.saveNoteImage(noteId, swingImage);
			dao.saveNoteImage(noteId, new Image(swingImage.getWidth(), swingImage.getHeight(), imageUrl));
		}		
	}
	
	private BufferedImage toSwingImage(MultipartFile image) throws IOException {
		InputStream imageInput = image.getInputStream();
		final BufferedImage swingImage = ImageIO.read(image.getInputStream());
		imageInput.close();
		return swingImage;
	}
}
