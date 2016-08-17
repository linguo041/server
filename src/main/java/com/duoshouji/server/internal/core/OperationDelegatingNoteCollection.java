package com.duoshouji.server.internal.core;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.duoshouji.server.internal.dao.NoteDto;
import com.duoshouji.server.internal.dao.NoteDtoCollection;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;

public class OperationDelegatingNoteCollection implements NoteCollection {

	private NoteDtoCollection noteCollectionDto;
	private UserNoteOperationManager operationDelegator;
	
	public OperationDelegatingNoteCollection(
			NoteDtoCollection noteCollectionDto,
			UserNoteOperationManager operationDelegator) {
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
