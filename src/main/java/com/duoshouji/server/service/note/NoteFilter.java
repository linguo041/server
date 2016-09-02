package com.duoshouji.server.service.note;

public class NoteFilter {

	private Tag tag;
	
	public NoteFilter(Tag tag) {
		super();
		this.tag = tag;
	}
	
	public Tag getTag() {
		return tag;
	}
	
	public boolean isTagSet() {
		return tag != null;
	}
}
