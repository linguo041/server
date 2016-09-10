package com.duoshouji.server.service.note;

public interface NoteRepository {

	NoteCollection listNotes(NoteFilter noteFilter);
	
	Note getNote(long noteId);
}
