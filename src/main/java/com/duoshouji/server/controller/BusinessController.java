package com.duoshouji.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.duoshouji.server.entity.Note;
import com.duoshouji.server.service.NoteService;

@RestController
public class BusinessController {

	@Autowired
	private NoteService noteService;
	
	@RequestMapping(value="/note/{noteId}", method=RequestMethod.GET)
	public Note getNoteById (@PathVariable(value="noteId") Long noteId) {
		List<Note> notes = noteService.getNoteByNoteId(noteId);
		
		if (notes != null && notes.size() > 0) {
			return notes.get(0);
		} else {
			return null;
		}
	}
}
