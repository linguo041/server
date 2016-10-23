package com.duoshouji.core.note;

import java.util.Iterator;

import com.duoshouji.core.NoteFilter;
import com.duoshouji.core.util.IndexRange;
import com.duoshouji.service.note.BasicNote;
import com.duoshouji.service.user.UserFacade;

public class FilteredNoteCollection extends AbstractOperationDelegatingNoteCollection {

	private NoteFilter noteFilter;
	private long followerId;
	
	public FilteredNoteCollection(NoteRepository operationDelegator, long cutoff, NoteFilter noteFilter, long followerId) {
		super(operationDelegator, cutoff);
		this.noteFilter = noteFilter;
		this.followerId = followerId;
	}
	
	
	public FilteredNoteCollection(NoteRepository operationDelegator, long cutoff, NoteFilter noteFilter) {
		this(operationDelegator, cutoff, noteFilter, UserFacade.NULL_USER_ID);
	}

	@Override
	protected Iterator<BasicNote> getNoteIterator(NoteRepository operationDelegator, long cutoff, IndexRange range) {
		return operationDelegator.findNotes(cutoff, range, noteFilter, followerId);
	}

}
