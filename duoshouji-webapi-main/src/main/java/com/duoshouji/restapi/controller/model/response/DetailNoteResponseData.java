package com.duoshouji.restapi.controller.model.response;

import java.math.BigDecimal;
import java.util.List;

import com.duoshouji.service.common.Tag;
import com.duoshouji.service.note.Note;
import com.duoshouji.service.util.Image;

public class DetailNoteResponseData {
	
	private List<Tag> tags;
	private Note delegator;
	
	public DetailNoteResponseData(Note delegator, List<Tag>	tags) {
		this.delegator = delegator;
		this.tags = tags;
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
		return tags;
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
	
	public NoteMark getNoteMark() {
		return new NoteMark();
	}
	
	public class NoteMark {
		
		public BigDecimal getPrice() {
			return delegator.getCommodity().getPrice();
		}
		
		public String getProductName() {
			return delegator.getCommodity().getProductName();
		}
		
		public String getLocation() {
			return delegator.getCommodity().getDistrict().toString();
		}
	}
}