package com.duoshouji.server.service.note;

import com.duoshouji.server.service.user.BasicUser;

public class NoteDetailAndOwner {

	private NoteDetail note;
	private BasicUser owner;
	
	public NoteDetailAndOwner(NoteDetail note, BasicUser owner) {
		super();
		this.note = note;
		this.owner = owner;
	}
	
	public NoteDetail getNote() {
		return note;
	}
	
	public BasicUser getOwner() {
		return owner;
	}

}
