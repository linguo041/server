package com.duoshouji.server.internal.note;

import com.duoshouji.server.service.note.CommentCollection;
import com.duoshouji.server.service.note.LikeCollection;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteAlbum;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteDao;
import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.user.RegisteredUser;

public class NoteOperationManager implements NoteRepository {

	private NoteDao noteDao;
	
	public NoteOperationManager(NoteDao noteDao) {
		super();
		this.noteDao = noteDao;
	}

	@Override
	public NoteCollection findNotes() {
		return new OperationDelegatingNoteCollection(noteDao.findNotes(), this);
	}

	NoteAlbum getNoteAlbum(OperationDelegatingNote operationDelegatingNote) {
		return noteDao.findNoteAlbum(operationDelegatingNote.noteDto);
	}

	RegisteredUser getOwner(OperationDelegatingNote operationDelegatingNote) {
		// TODO Auto-generated method stub
		return null;
	}

	LikeCollection getLiks(OperationDelegatingNote operationDelegatingNote) {
		// TODO Auto-generated method stub
		return null;
	}

	CommentCollection getComments(OperationDelegatingNote operationDelegatingNote) {
		// TODO Auto-generated method stub
		return null;
	}

	OperationDelegatingNote newNote(NoteDto noteDto) {
		return new OperationDelegatingNote(noteDto, this);
	}
	
}
