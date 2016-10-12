package com.duoshouji.service.note;

import com.duoshouji.service.annotation.Collection;

@Collection
public interface NoteCollection extends Iterable<BasicNote> {

	NoteCollection subCollection(int startIndex, int endIndex);

}
