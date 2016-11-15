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
		return delegator.getLocation().getLongitude();
	}
	
	public BigDecimal getLatitude() {
		return delegator.getLocation().getLatitude();
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