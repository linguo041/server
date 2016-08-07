package com.duoshouji.server.internal.note;

import org.springframework.beans.factory.annotation.Autowired;

import com.duoshouji.server.service.note.NoteFacade;
import com.duoshouji.server.service.note.NoteRepository;

public class NoteFacadeImpl implements NoteFacade {

	private NoteRepository noteRepository;
	
	@Autowired
	public NoteFacadeImpl(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}

	@Override
	public PushedNoteRequestor getPushedNoteRequestor() {
		// TODO Auto-generated method stub
		return null;
	}

}
