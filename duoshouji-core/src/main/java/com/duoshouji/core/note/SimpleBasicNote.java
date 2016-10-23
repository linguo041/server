package com.duoshouji.core.note;

import com.duoshouji.core.FullFunctionalUser;
import com.duoshouji.service.note.BasicNote;
import com.duoshouji.service.util.Image;

class SimpleBasicNote implements BasicNote {
	
	final NoteRepository operationManager;
	final long noteId;
	
	String title;
	Image mainImage;
	int ownerRating;
	int commentRatingSum;
	int likeCount;
	int commentCount;
	int transactionCount;
	long publishedTime;
	long authorId;
	
	SimpleBasicNote(long noteId, NoteRepository operationManager) {
		this.noteId = noteId;
		this.operationManager = operationManager;
	}

	@Override
	public long getNoteId() {
		return noteId;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public Image getMainImage() {
		return mainImage;
	}

	@Override
	public int getRating() {
		if (commentCount == 0) {
			return ownerRating;
		}
		return (commentRatingSum + ownerRating) / (commentCount + 1);
	}

	@Override
	public int getLikeCount() {
		return likeCount;
	}

	@Override
	public int getCommentCount() {
		return commentCount;
	}

	@Override
	public int getTransactionCount() {
		return transactionCount;
	}

	@Override
	public long getPublishedTime() {
		return publishedTime;
	}

	@Override
	public FullFunctionalUser getAuthor() {
		return operationManager.getAuthor(this);
	}
}
