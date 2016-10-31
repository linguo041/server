package com.duoshouji.restapi.controller.model.response;

import com.duoshouji.service.note.NoteComment;

public class NoteCommentResult {
	
	private NoteComment delegator;

	public NoteCommentResult(NoteComment delegator) {
		this.delegator = delegator;
	}
	
	public String getComment() {
		return delegator.getComment();
	}
	
	public String getAuthorNickname() {
		return delegator.getAuthor().getNickname();
	}
}