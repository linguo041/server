package com.duoshouji.server.service.note;

public interface NoteRepository {

	NoteCollection listSquareNotes(NoteFilter noteFilter);
	
	Note getNote(long noteId);
}
