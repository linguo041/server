package com.duoshouji.restapi.controller.model;

import com.duoshouji.service.note.BasicNote;

public class BasicNoteResult {
	
	private BasicNote delegator;

	public BasicNoteResult(BasicNote delegator) {
		this.delegator = delegator;
	}

	public long getNoteId() {
		return delegator.getNoteId();
	}

	public long getPublishedTime() {
		return delegator.getPublishedTime();
	}

	public String getTitle() {
		return delegator.getTitle();
	}

	public String getImage() {
		return delegator.getMainImage().getUrl();
	}

	public int getImageWidth() {
		return delegator.getMainImage().getWidth();
	}

	public int getImageHeight() {
		return delegator.getMainImage().getHeight();
	}

	public String getPortrait() {
		return delegator.getAuthor().getPortrait().getUrl();
	}

	public int getRating() {
		return delegator.getRating();
	}

	public int getLikeCount() {
		return delegator.getLikeCount();
	}

	public int getCommentCount() {
		return delegator.getCommentCount();
	}

	public int getTransactionCount() {
		return delegator.getTransactionCount();
	}

}