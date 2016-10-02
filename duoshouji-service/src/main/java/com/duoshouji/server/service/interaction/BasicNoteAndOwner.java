package com.duoshouji.server.service.interaction;

import com.duoshouji.server.service.note.BasicNote;
import com.duoshouji.server.service.user.BasicUser;

public class BasicNoteAndOwner {
	
	private BasicNote note;
	private BasicUser owner;
	
	public BasicNoteAndOwner(BasicNote note, BasicUser owner) {
		super();
		this.note = note;
		this.owner = owner;
	}
	
	public BasicNote getNote() {
		return note;
	}
	
	public BasicUser getOwner() {
		return owner;
	}
	
}
