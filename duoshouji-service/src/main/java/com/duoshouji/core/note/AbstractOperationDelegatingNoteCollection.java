package com.duoshouji.core.note;

import java.util.Collections;
import java.util.Iterator;

import com.duoshouji.core.util.IndexRange;
import com.duoshouji.service.note.BasicNote;
import com.duoshouji.service.note.NoteCollection;

public abstract class AbstractOperationDelegatingNoteCollection implements NoteCollection {

	private static final NoteCollection EMPTY_COLLECTION =
			new NoteCollection() {

				@Override
				public Iterator<BasicNote> iterator() {
					return Collections.emptyIterator();
				}

				@Override
				public NoteCollection subCollection(int startIndex, int endIndex) {
					return this;
				}
		
			};
	
	private final long cutoff;
	private final NoteRepository operationDelegator;
	
	public AbstractOperationDelegatingNoteCollection(NoteRepository operationDelegator, long cutoff) {
		super();
		this.operationDelegator = operationDelegator;
		this.cutoff = cutoff;
	}

	protected abstract Iterator<BasicNote> getNoteIterator(NoteRepository operationDelegator, long cutoff, IndexRange range);
	
	@Override
	public Iterator<BasicNote> iterator() {
		return getNoteIterator(operationDelegator, cutoff, null);
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
		public Iterator<BasicNote> iterator() {
			return getNoteIterator(operationDelegator, cutoff, new IndexRange(startIndex, endIndex));
		}

		@Override
		public NoteCollection subCollection(int startIndex, int endIndex) {
			return AbstractOperationDelegatingNoteCollection.this.subCollection(
					this.startIndex + startIndex, this.startIndex + endIndex);
		}
		
	}
}
