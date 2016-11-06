package com.duoshouji.service.note;

import com.duoshouji.service.annotation.Collection;

@Collection
public interface NoteCollection extends Iterable<Note> {

	NoteCollection subCollection(int startIndex, int endIndex);

}
