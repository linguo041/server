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
import com.duoshouji.server.rest.InvalidRequestParameterFormatException;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteFacade;
import com.duoshouji.server.service.note.NoteFacade.PushedNoteRequestor;
import com.duoshouji.server.service.user.UserFacade;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.Location;

@RequestMapping("/notes")
@RestController
public class NoteResource {
	
	private NoteFacade noteFacade;
	private UserFacade userFacade;
	
	@Autowired
	public NoteResource(NoteFacade noteFacade, UserFacade userFacade) {
		super();
		this.noteFacade = noteFacade;
		this.userFacade = userFacade;
	}

	@RequestMapping(path = "/", method = RequestMethod.GET)
	public List<NoteJson> getNotes(
			@RequestAttribute("user") UserIdentifier userId,
			@RequestParam("filter") String filter,
			@RequestParam("category") String category) {
		
		PushedNoteRequestor requestor = noteFacade.getPushedNoteRequestor();
		try {
			if (filter != null) {
				if (Constants.NOTE_FILTER_MARKED.equals(filter)) {
					requestor.setUserMarked(userFacade.getUser(userId));
				} else {
					requestor.setLocation(Location.parse(filter));
				}
			}
			if (category != null) {
				requestor.setCategory(Integer.parseInt(category));
			}
		} catch (Exception e) {
			throw new InvalidRequestParameterFormatException(e);
		}
		NoteCollection notes = requestor.getPushedNotes();
		List<NoteJson> returnValue = new ArrayList<NoteJson>(notes.size());
		for (Note note : notes) {
			NoteJson noteJson = new NoteJson();
			noteJson.setCommentCount(note.getComments().size());
			noteJson.setDiscountPrice(note.getDiscountPrice());
			noteJson.setImage(note.getNoteAlbum().getMainImage().getURL());
			noteJson.setLikeCount(note.getLikes().size());
			noteJson.setListPrice(note.getListPrice());
			noteJson.setNoteId(note.getNoteId());
			noteJson.setPortrait(note.getOwner().getPortrait().getURL());
			noteJson.setRank(note.getRank());
			noteJson.setTitle(note.getTitle());
			returnValue.add(noteJson);
		}
		return returnValue;
	}
}
