package com.duoshouji.server.internal.core;

import java.util.Collections;
import java.util.Iterator;

import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;

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
	
	private long cutoff;
	private UserNoteOperationManager operationDelegator;
	
	public OperationDelegatingNoteCollection(UserNoteOperationManager operationDelegator, long cutoff) {
		super();
		this.operationDelegator = operationDelegator;
		this.cutoff = cutoff;
	}

	@Override
	public Iterator<Note> iterator() {
		return operationDelegator.findNotes(cutoff);
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
			return operationDelegator.findNotes(cutoff, startIndex, endIndex);
		}

		@Override
		public NoteCollection subCollection(int startIndex, int endIndex) {
			return OperationDelegatingNoteCollection.this.subCollection(
					this.startIndex + startIndex, this.startIndex + endIndex);
		}
		
	}
}
