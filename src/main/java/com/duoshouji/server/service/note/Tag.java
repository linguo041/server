package com.duoshouji.server.service.note;

public final class Tag {
		
	private long tagId;
	private String tagName;
	
	Tag(long tagId, String tagName) {
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

	@Override
	public int hashCode() {
		return Long.hashCode(tagId);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}
}
