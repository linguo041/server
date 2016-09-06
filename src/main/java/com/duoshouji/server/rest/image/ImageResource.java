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

import com.duoshouji.server.service.image.ImageStore;
import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.MobileNumber;

@RestController
public class ImageResource {

	private UserRepository userRepository;
	private NoteRepository noteRepository;
	private ImageStore imageStore;
	
	@Autowired
	public ImageResource(UserRepository userRepository,
			NoteRepository noteRepository, ImageStore imageStore) {
		super();
		this.userRepository = userRepository;
		this.noteRepository = noteRepository;
		this.imageStore = imageStore;
	}

	@RequestMapping(path = "/accounts/{account-id}/settings/profile/protrait", method = RequestMethod.POST)
	public void uploadPortrait(
			@RequestParam("image") MultipartFile image,
			@PathVariable("account-id") MobileNumber accountId) throws IOException {
		if (!image.isEmpty()) {
			BufferedImage swingImage = toSwingImage(image);
			String imageUrl = imageStore.savePortrait(accountId.toString(), swingImage);
			userRepository.findUser(accountId).setPortrait(new Image(swingImage.getWidth(), swingImage.getHeight(), imageUrl));
		}
	}
	
	@RequestMapping(path = "/notes/{note-id}/images/main-image", method = RequestMethod.POST)
	public void uploadNoteImage(
			@RequestParam("image") MultipartFile image,
			@PathVariable("note-id") long noteId
			) throws IOException {
		if (!image.isEmpty()) {
			BufferedImage swingImage = toSwingImage(image);
			String imageUrl = imageStore.saveNoteImage(noteId, swingImage);
			noteRepository.getNote(noteId).setMainImage(new Image(swingImage.getWidth(), swingImage.getHeight(), imageUrl));
		}		
	}
	
	private BufferedImage toSwingImage(MultipartFile image) throws IOException {
		InputStream imageInput = image.getInputStream();
		final BufferedImage swingImage = ImageIO.read(image.getInputStream());
		imageInput.close();
		return swingImage;
	}
}
