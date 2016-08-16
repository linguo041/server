package com.duoshouji.server.internal.core;

import com.duoshouji.server.internal.note.NoteDto;
import com.duoshouji.server.service.note.CommentCollection;
import com.duoshouji.server.service.note.LikeCollection;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteAlbum;
import com.duoshouji.server.service.user.RegisteredUser;

class OperationDelegatingNote implements Note {

	NoteDto noteDto;
	private UserNoteOperationManager operationDelegator;
	
	public OperationDelegatingNote(NoteDto noteDto,
			UserNoteOperationManager operationDelegator) {
		super();
		this.noteDto = noteDto;
		this.operationDelegator = operationDelegator;
	}

	@Override
	public long getNoteId() {
		return noteDto.getNoteId();
	}

	@Override
	public String getTitle() {
		return noteDto.getTitle();
	}

	@Override
	public NoteAlbum getNoteAlbum() {
		return operationDelegator.getNoteAlbum(this);
	}

	@Override
	public RegisteredUser getOwner() {
		return operationDelegator.getOwner(this);
	}

	@Override
	public int getRank() {
		return noteDto.getRank();
	}

	@Override
	public LikeCollection getLikes() {
		return operationDelegator.getLiks(this);
	}

	@Override
	public CommentCollection getComments() {
		return operationDelegator.getComments(this);
	}
}
