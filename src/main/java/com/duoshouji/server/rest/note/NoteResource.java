package com.duoshouji.server.rest.note;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.rest.Constants;
import com.duoshouji.server.rest.StandardJsonResponse;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteFacade;
import com.duoshouji.server.service.user.RegisteredUser;

@RestController
public class NoteResource {
	
	private NoteFacade noteFacade;
	
	@Autowired
	public NoteResource(NoteFacade noteFacade) {
		super();
		this.noteFacade = noteFacade;
	}

	@RequestMapping(path = "/notes", method = RequestMethod.GET)
	public StandardJsonResponse getNotes(
			@RequestAttribute(name=Constants.USER_ID_ATTRIBUTE) RegisteredUser user,
			@RequestParam("loadedSize") int loadedSize,
			@RequestParam("pageSize") int pageSize
			) {
		NoteCollection notes;
		if (loadedSize < 0) {
			notes = noteFacade.pushSquareNotes(user);
			loadedSize = 0;
		} else {
			notes = noteFacade.getPushedSquareNotes(user);
		}
		notes = notes.subCollection(loadedSize, loadedSize + pageSize);
		List<NoteJson> returnValue = new ArrayList<NoteJson>();
		for (Note note : notes) {
			returnValue.add(convert(note));
		}
		return StandardJsonResponse.wrapResponse(returnValue);
	}
	
	private NoteJson convert(Note note) {
		NoteJson noteJson = new NoteJson();
		noteJson.setNoteId(note.getNoteId());
		noteJson.setTitle(note.getTitle());
		noteJson.setImage(note.getNoteAlbum().getMainImage().getUrl());
		noteJson.setImageWidth(note.getNoteAlbum().getMainImage().getWidth());
		noteJson.setImageHeight(note.getNoteAlbum().getMainImage().getHeight());
		noteJson.setPortrait(note.getOwner().getPortrait().getUrl());
		noteJson.setRank(note.getRank());
		noteJson.setLikeCount(note.getLikes().size());
		noteJson.setCommentCount(note.getComments().size());
		return noteJson;
	}
}
