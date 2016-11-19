package com.duoshouji.restapi.controller.model.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.duoshouji.restapi.image.ImageJsonAdapter;
import com.duoshouji.restapi.image.ImageMark;
import com.duoshouji.service.common.Tag;
import com.duoshouji.service.note.Note;
import com.duoshouji.service.note.NoteImage;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DetailNoteResponseData {
	
	private List<Tag> tags;
	private Note delegator;
	private Long userId;
	
	public DetailNoteResponseData(Note delegator, List<Tag>	tags) {
		this(delegator, tags, null);
	}
	
	public DetailNoteResponseData(Note delegator, List<Tag>	tags, Long userId) {
		this.delegator = delegator;
		this.tags = tags;
		this.userId = userId;
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
	
	public List<NoteImageAdapter> getImages() {
		List<NoteImageAdapter> adapters = new ArrayList<NoteImageAdapter>();
		for (NoteImage noteImage : delegator.getImages()) {
			adapters.add(new NoteImageAdapter(noteImage));
		}
		return adapters;
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
	
	public String getAddress() {
		return delegator.getAddress();
	}
	
	public BigDecimal getLongitude() {
		if (delegator.getLocation() != null) {
			return delegator.getLocation().getLongitude();
		}
		return null;
	}
	
	public BigDecimal getLatitude() {
		if (delegator.getLocation() != null) {
			return delegator.getLocation().getLatitude();
		}
		return null;
	}
	
	public boolean getIsLikedByVisitor() {
		if (userId == null) {
			return false;
		}
		return delegator.isLikedBy(userId.longValue());
	}
	
	public class NoteImageAdapter {
		
		private NoteImage noteImage;
		
		public NoteImageAdapter(NoteImage noteImage) {
			this.noteImage = noteImage;
		}

		public ImageJsonAdapter getImage() {
			return new ImageJsonAdapter(noteImage);
		}
		
		public List<Object> getMarks() throws Exception {
			if (noteImage.getMarks() == null) {
				return Collections.emptyList();
			}
			return new ObjectMapper().readerFor(ImageMark.class).readValues(noteImage.getMarks()).readAll();
		}
	}
}