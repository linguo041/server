package com.duoshouji.server.service.note;

public interface NoteRepository {

	NoteCollection findNotes();

	NoteCollection findNotes(Tag tag);
}
