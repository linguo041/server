package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.dao.NoteDto;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.user.BasicUserAttributes;
import com.duoshouji.server.util.Image;

class OperationDelegatingNote implements Note {

	private UserNoteOperationManager operationDelegator;
	NoteDto noteDto;
	
	public OperationDelegatingNote(NoteDto noteDto,
			UserNoteOperationManager operationDelegator) {
		super();
		this.noteDto = noteDto;
		this.operationDelegator = operationDelegator;
	}

	@Override
	public long getNoteId() {
		return noteDto.noteId;
	}

	@Override
	public String getTitle() {
		return noteDto.title;
	}

	@Override
	public BasicUserAttributes getOwner() {
		return operationDelegator.getOwner(this);
	}

	@Override
	public int getRank() {
		return noteDto.rank;
	}

	@Override
	public Image getMainImage() {
		return noteDto.mainImage;
	}

	@Override
	public int getLikeCount() {
		return noteDto.likeCount;
	}

	@Override
	public int getCommentCount() {
		return noteDto.commentCount;
	}

	@Override
	public int getTransactionCount() {
		return noteDto.transactionCount;
	}

	@Override
	public long getPublishedTime() {
		return noteDto.publishedTime;
	}
}
