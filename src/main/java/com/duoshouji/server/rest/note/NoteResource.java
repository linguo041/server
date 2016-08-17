package com.duoshouji.server.rest.note;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.rest.StandardJsonResponse;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteFacade;

@RequestMapping("/notes")
@RestController
public class NoteResource {
	
	private NoteFacade noteFacade;
	
	@Autowired
	public NoteResource(NoteFacade noteFacade) {
		super();
		this.noteFacade = noteFacade;
	}

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public StandardJsonResponse getNotes() {
		NoteCollection notes = noteFacade.getPushedNotes();
		List<NoteJson> returnValue = new ArrayList<NoteJson>();
		for (Note note : notes) {
			NoteJson noteJson = new NoteJson();
			noteJson.setNoteId(note.getNoteId());
			noteJson.setTitle(note.getTitle());
			noteJson.setImage(note.getNoteAlbum().getMainImage().getURL());
			noteJson.setImageWidth(note.getNoteAlbum().getMainImage().getWidth());
			noteJson.setImageHeight(note.getNoteAlbum().getMainImage().getHeight());
			noteJson.setPortrait(note.getOwner().getPortrait().getURL());
			noteJson.setRank(note.getRank());
			noteJson.setLikeCount(note.getLikes().size());
			noteJson.setCommentCount(note.getComments().size());
			returnValue.add(noteJson);
		}
		return StandardJsonResponse.wrapResponse(returnValue);
	}
}
