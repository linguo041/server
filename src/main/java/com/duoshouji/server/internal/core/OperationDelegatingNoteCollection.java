package com.duoshouji.server.internal.core;

import java.util.Iterator;

import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;

public class OperationDelegatingNoteCollection implements NoteCollection {

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
		return new SubCollection(startIndex < 0 ? 0: startIndex, endIndex);
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
			
		}
		
	}
}
