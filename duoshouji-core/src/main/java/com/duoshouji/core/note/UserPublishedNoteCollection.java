package com.duoshouji.core.note;

import java.util.Iterator;

import com.duoshouji.core.util.IndexRange;
import com.duoshouji.service.note.Note;

public class UserPublishedNoteCollection extends
		AbstractOperationDelegatingNoteCollection {
	private long authorId;
	
	public UserPublishedNoteCollection(NoteRepository operationDelegator, long cutoff, long authorId) {
		super(operationDelegator, cutoff);
		this.authorId = authorId;
	}
	@Override
	protected Iterator<Note> getNoteIterator(NoteRepository operationDelegator, long cutoff, IndexRange range) {
		return operationDelegator.findNotes(cutoff, range, authorId);
	}

}
