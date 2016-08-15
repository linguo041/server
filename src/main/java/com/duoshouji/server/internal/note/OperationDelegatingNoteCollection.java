package com.duoshouji.server.internal.note;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;

public class OperationDelegatingNoteCollection implements NoteCollection {

	private NoteCollectionDto noteCollectionDto;
	private NoteOperationManager operationDelegator;
	
	public OperationDelegatingNoteCollection(
			NoteCollectionDto noteCollectionDto,
			NoteOperationManager operationDelegator) {
		super();
		this.noteCollectionDto = noteCollectionDto;
		this.operationDelegator = operationDelegator;
	}

	@Override
	public Iterator<Note> iterator() {
		return new Iterator<Note>(){
			Iterator<NoteDto> innerIte = noteCollectionDto.iterator();
			@Override
			public boolean hasNext() {
				return innerIte.hasNext();
			}

			@Override
			public Note next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				return operationDelegator.newNote(innerIte.next());
			}
			
		};
	}

}
