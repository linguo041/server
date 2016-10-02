package com.duoshouji.server.service.note;

import java.util.List;

public interface NoteRepository {

	NoteCollection listSquareNotes(NoteFilter noteFilter, long timestamp);
	
	Note getNote(long noteId);

	List<NoteComment> getNoteComments(long noteId);
}
