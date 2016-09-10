package com.duoshouji.server.service.note;

import com.duoshouji.server.annotation.Collection;

@Collection
public interface NoteCollection extends Iterable<BasicNote> {

	NoteCollection subCollection(int startIndex, int endIndex);

}
