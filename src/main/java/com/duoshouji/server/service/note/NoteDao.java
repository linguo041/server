package com.duoshouji.server.service.note;

import com.duoshouji.server.internal.note.NoteCollectionDto;

public interface NoteDao {

	NoteCollectionDto findNotes();

}
