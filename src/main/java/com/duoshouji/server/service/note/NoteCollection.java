package com.duoshouji.server.service.note;


public interface NoteCollection extends Iterable<Note> {

	NoteCollection subCollection(int startIndex, int endIndex);

}
