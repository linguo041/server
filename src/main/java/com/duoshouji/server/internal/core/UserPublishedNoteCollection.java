package com.duoshouji.server.internal.core;

import java.util.Iterator;

import com.duoshouji.server.service.note.BasicNote;
import com.duoshouji.server.util.IndexRange;
import com.duoshouji.server.util.MobileNumber;

public class UserPublishedNoteCollection extends
		AbstractOperationDelegatingNoteCollection {
	private MobileNumber userId;
	
	public UserPublishedNoteCollection(
			UserNoteOperationManager operationDelegator, long cutoff, MobileNumber userId) {
		super(operationDelegator, cutoff);
		this.userId = userId;
	}
	@Override
	protected Iterator<BasicNote> getNoteIterator(
			UserNoteOperationManager operationDelegator, long cutoff, IndexRange range) {
		return operationDelegator.findNotes(cutoff, range, userId);
	}

}
