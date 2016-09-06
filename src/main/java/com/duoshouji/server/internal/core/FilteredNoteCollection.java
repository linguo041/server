package com.duoshouji.server.internal.core;

import java.util.Iterator;

import com.duoshouji.server.service.note.BasicNote;
import com.duoshouji.server.service.note.NoteFilter;
import com.duoshouji.server.util.IndexRange;

public class FilteredNoteCollection extends AbstractOperationDelegatingNoteCollection {

	private NoteFilter noteFilter;
	
	public FilteredNoteCollection(
			UserNoteOperationManager operationDelegator, long cutoff, NoteFilter noteFilter) {
		super(operationDelegator, cutoff);
		this.noteFilter = noteFilter;
	}

	@Override
	protected Iterator<BasicNote> getNoteIterator(
			UserNoteOperationManager operationDelegator, long cutoff, IndexRange range) {
		return operationDelegator.findNotes(cutoff, range, noteFilter);
	}

}
