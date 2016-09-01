package com.duoshouji.server.internal.core;

import java.util.Collections;
import java.util.Iterator;

import com.duoshouji.server.service.interaction.NoteFilter;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.util.IndexRange;

public class OperationDelegatingNoteCollection implements NoteCollection {

	private static final NoteCollection EMPTY_COLLECTION =
			new NoteCollection() {

				@Override
				public Iterator<Note> iterator() {
					return Collections.emptyIterator();
				}

				@Override
				public NoteCollection subCollection(int startIndex, int endIndex) {
					return this;
				}
		
			};
	
	private final long cutoff;
	private final UserNoteOperationManager operationDelegator;
	private NoteFilter filter;
	
	public OperationDelegatingNoteCollection(UserNoteOperationManager operationDelegator, long cutoff, NoteFilter filter) {
		super();
		this.operationDelegator = operationDelegator;
		this.cutoff = cutoff;
		this.filter = filter;
	}

	@Override
	public Iterator<Note> iterator() {
		return operationDelegator.findNotes(cutoff, null, filter);
	}

	@Override
	public NoteCollection subCollection(int startIndex, int endIndex) {
		if (startIndex < 0) {
			startIndex = 0;
		}
		if (endIndex <= startIndex) {
			return EMPTY_COLLECTION;
		}
		return new SubCollection(startIndex, endIndex);
	}

	private class SubCollection implements NoteCollection {
		int startIndex, endIndex;
		
		public SubCollection(int startIndex, int endIndex) {
			super();
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		@Override
		public Iterator<Note> iterator() {
			return operationDelegator.findNotes(cutoff, new IndexRange(startIndex, endIndex), filter);
		}

		@Override
		public NoteCollection subCollection(int startIndex, int endIndex) {
			return OperationDelegatingNoteCollection.this.subCollection(
					this.startIndex + startIndex, this.startIndex + endIndex);
		}
		
	}
}
