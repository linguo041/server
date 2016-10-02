package com.duoshouji.server.internal.core;

import com.duoshouji.server.service.note.NoteComment;
import com.duoshouji.util.MobileNumber;

public class AuthorIdAttachedNoteComment implements NoteComment {
	private MobileNumber authorId;
	private String comment;
	
	AuthorIdAttachedNoteComment(MobileNumber authorId, String comment) {
		this.authorId = authorId;
		this.comment = comment;
	}

	@Override
	public String getComment() {
		return comment;
	}
	
	MobileNumber getAuthorId() {
		return authorId;
	}
}
