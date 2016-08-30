package com.duoshouji.server.service.note;

import com.duoshouji.server.util.MobileNumber;

public class NoteFilter {

	private MobileNumber ownerId;
	private Tag tag;
	
	public NoteFilter(Tag tag) {
		super();
		this.tag = tag;
	}

	public NoteFilter(MobileNumber ownerId) {
		super();
		this.ownerId = ownerId;
	}

	public MobileNumber getOwnerId() {
		return ownerId;
	}
	
	public boolean isSetOwnerId() {
		return ownerId != null;
	}
	
	public Tag getTag() {
		return tag;
	}
	
	public boolean isSetTag() {
		return tag != null;
	}
	
}
