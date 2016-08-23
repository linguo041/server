package com.duoshouji.server.service.note;

import com.duoshouji.server.util.MobileNumber;

public class NoteFilter {

	private MobileNumber ownerId;

	public NoteFilter(MobileNumber ownerId) {
		super();
		this.ownerId = ownerId;
	}

	public MobileNumber getOwnerId() {
		return ownerId;
	}
	
}
