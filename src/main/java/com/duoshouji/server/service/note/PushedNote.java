package com.duoshouji.server.service.note;

import com.duoshouji.server.service.user.BasicUser;
import com.duoshouji.server.util.Image;

public class PushedNote implements Note {

	private Note delegator;
	private BasicUser owner;
	
	public PushedNote(Note delegator, BasicUser owner) {
		super();
		this.delegator = delegator;
		this.owner = owner;
	}

	@Override
	public long getNoteId() {
		return delegator.getNoteId();
	}

	@Override
	public String getTitle() {
		return delegator.getTitle();
	}

	@Override
	public Image getMainImage() {
		return delegator.getMainImage();
	}

	@Override
	public int getRank() {
		return delegator.getRank();
	}

	@Override
	public int getLikeCount() {
		return delegator.getLikeCount();
	}

	@Override
	public int getCommentCount() {
		return delegator.getCommentCount();
	}

	@Override
	public int getTransactionCount() {
		return delegator.getTransactionCount();
	}

	@Override
	public long getPublishedTime() {
		return delegator.getPublishedTime();
	}

	public BasicUser getOwner() {
		return owner;
	}
}
