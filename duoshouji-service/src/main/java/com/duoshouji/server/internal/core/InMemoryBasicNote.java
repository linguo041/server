package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.note.BasicNote;
import com.duoshouji.server.service.user.BasicUser;
import com.duoshouji.server.util.Image;

class InMemoryBasicNote implements BasicNote, UserNoteInteractionAware {

	private final long noteId;
	String title;
	Image mainImage;
	int ownerRating;
	int commentRatingSum;
	int likeCount;
	int commentCount;
	int transactionCount;
	long publishedTime;
	BasicUser owner;
	
	InMemoryBasicNote(long noteId) {
		this.noteId = noteId;
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
	public void fireAddComment(int rating) {
		this.commentRatingSum += rating;
		++commentCount;
	}

	@Override
	public void fireAddLike() {
		++likeCount;
	}
}
