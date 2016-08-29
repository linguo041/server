package com.duoshouji.server.service.note;

import com.duoshouji.server.util.MobileNumber;

public class NoteFilter {

	private MobileNumber ownerId;
	private Long tagId;
	
	
	
	public MobileNumber getOwnerId() {
		return ownerId;
	}
	
	public long getTagId() {
		return tagId.longValue();
	}
	
}
