package com.duoshouji.server.internal.core;

import java.util.Iterator;

import com.duoshouji.server.service.note.BasicNote;
import com.duoshouji.server.service.note.NoteFilter;
import com.duoshouji.server.util.IndexRange;
import com.duoshouji.util.MobileNumber;

public class FilteredNoteCollection extends AbstractOperationDelegatingNoteCollection {

	private NoteFilter noteFilter;
	private MobileNumber userId;
	
	public FilteredNoteCollection(
			UserNoteOperationManager operationDelegator, long cutoff, NoteFilter noteFilter, MobileNumber userId) {
		super(operationDelegator, cutoff);
		this.noteFilter = noteFilter;
		this.userId = userId;
	}
	
	public FilteredNoteCollection(
			UserNoteOperationManager operationDelegator, long cutoff, NoteFilter noteFilter) {
		this(operationDelegator, cutoff, noteFilter, null);
	}

	@Override
	protected Iterator<BasicNote> getNoteIterator(
			UserNoteOperationManager operationDelegator, long cutoff, IndexRange range) {
		return operationDelegator.findNotes(cutoff, range, noteFilter, userId);
	}

}
