package com.duoshouji.restapi.controller.model.response;

import java.util.List;

import com.duoshouji.service.common.Tag;
import com.duoshouji.service.note.NoteDetail;
import com.duoshouji.service.util.Image;

public class DetailNoteResult {
	
	private NoteDetail delegator;
	
	public DetailNoteResult(NoteDetail delegator) {
		this.delegator = delegator;
	}
	
	public long getUserId() {
		return delegator.getAuthor().getUserId();
	}
	
	public String getNickname() {
		return delegator.getAuthor().getNickname();
	}
	
	public String getPortrait() {
		return delegator.getAuthor().getPortrait().getUrl();
	}
	
	public long getNoteId() {
		return delegator.getNoteId();
	}
	
	public String getTitle() {
		return delegator.getTitle();
	}
	
	public long getPublishTime() {
		return delegator.getPublishedTime();
	}
	
	public List<Image> getImages() {
		return delegator.getImages();
	}
	
	public String getContent() {
		return delegator.getContent();
	}
	
	public List<Tag> getTags() {
		return delegator.getTags();
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
	
	public int getRating() {
		return delegator.getRating();
	}
}