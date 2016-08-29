package com.duoshouji.server.service.note;

public class Tag {
		
	private long tagId;
	private String tagName;
	
	public Tag(long tagId, String tagName) {
		super();
		this.tagId = tagId;
		this.tagName = tagName;
	}
	
	public long getTagId() {
		return tagId;
	}
	public String getTagName() {
		return tagName;
	}
	
	
}
