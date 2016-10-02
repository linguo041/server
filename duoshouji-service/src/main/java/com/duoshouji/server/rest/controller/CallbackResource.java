package com.duoshouji.server.rest.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.util.Image;
import com.duoshouji.util.MobileNumber;

@RestController
@RequestMapping(path = "/callback")
public class CallbackResource {

	private UserRepository userRepository;
	private NoteRepository noteRepository;
	
	@Autowired
	public CallbackResource(UserRepository userRepository, NoteRepository noteRepository) {
		this.userRepository = userRepository;
		this.noteRepository = noteRepository;
	}

	@RequestMapping(path = "/accounts/{account-id}/settings/profile/protrait", method = RequestMethod.POST)
	public void uploadUserPortrait(
			@PathVariable("account-id") MobileNumber userId,
			@RequestParam("imageUrl") String imageUrl,
			@RequestParam("imageWidth") int imageWidth,
			@RequestParam("imageHeight") int imageHeight) throws IOException {
		final Image image = new Image(imageWidth, imageHeight, imageUrl);
		userRepository.findUser(userId).setPortrait(image);
	}
	
	@RequestMapping(path = "/notes/{note-id}/images", method = RequestMethod.POST)
	public void uploadNoteImage(
			@PathVariable("note-id") long noteId,
			@RequestParam("imageCount") int imageCount,
			@RequestParam("imageUrl") String[] imageUrls,
			@RequestParam("imageWidth") int[] imageWidths,
			@RequestParam("imageHeight") int[] imageHeights) throws IOException {
		Image[] images = new Image[imageCount];
		for (int i = 0; i < imageCount; ++i) {
			images[i] = new Image(imageWidths[i], imageHeights[i], imageUrls[i]);
		}
		noteRepository.getNote(noteId).setImages(images);
	}
}
