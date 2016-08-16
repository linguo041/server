package com.duoshouji.server.internal.note;

import com.duoshouji.server.service.note.NoteCollection;

public interface NoteRepository {

	NoteCollection findNotes();

}
