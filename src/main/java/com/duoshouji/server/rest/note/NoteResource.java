package com.duoshouji.server.rest.note;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.rest.Constants;
import com.duoshouji.server.rest.StandardJsonResponse;
import com.duoshouji.server.service.DuoShouJiFacade;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;

@RestController
public class NoteResource {
	
	private DuoShouJiFacade noteFacade;
	
	@Autowired
	private NoteResource(DuoShouJiFacade noteFacade) {
		super();
		this.noteFacade = noteFacade;
	}

	@RequestMapping(path = "/notes", method = RequestMethod.GET)
	public StandardJsonResponse getNotes(
			@RequestHeader(name=Constants.APP_TOKEN_HTTP_HEADER_NAME) String token,
			@RequestParam("loadedSize") int loadedSize,
			@RequestParam("pageSize") int pageSize
			) {
		NoteCollection notes;
		if (loadedSize < 0) {
			notes = noteFacade.pushSquareNotes(token);
			loadedSize = 0;
		} else {
			notes = noteFacade.getPushedSquareNotes(token);
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
		noteJson.setImage(note.getMainImage().getUrl());
		noteJson.setImageWidth(note.getMainImage().getWidth());
		noteJson.setImageHeight(note.getMainImage().getHeight());
		noteJson.setPortrait(note.getOwner().getPortrait().getUrl());
		noteJson.setRank(note.getRank());
		noteJson.setLikeCount(note.getLikeCount());
		noteJson.setCommentCount(note.getCommentCount());
		return noteJson;
	}
}
