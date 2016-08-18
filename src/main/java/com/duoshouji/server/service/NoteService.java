package com.duoshouji.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoshouji.server.db.dao.NoteDao;
import com.duoshouji.server.entity.Note;

@Service
public class NoteService {

	@Autowired
	private NoteDao noteDao;
	
	public List<Note> getNoteByNoteId (Long noteId) {
		return noteDao.getNoteByNoteId(noteId);
	}
}
