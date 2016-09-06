package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.dao.NoteDto;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.util.Image;

class OperationDelegatingNote implements Note {

	private UserNoteOperationManager operationManager;
	NoteDto noteDto;

	public OperationDelegatingNote(UserNoteOperationManager operationManager,
			NoteDto noteDto) {
		super();
		this.operationManager = operationManager;
		this.noteDto = noteDto;
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
	public int getRank() {
		return noteDto.rank;
	}

	@Override
	public Image getMainImage() {
		return noteDto.mainImage;
	}

	@Override
	public void setMainImage(Image mainImage) {
		operationManager.setMainImage(this, mainImage);
		noteDto.mainImage = mainImage;
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
