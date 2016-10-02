package com.duoshouji.server.service.interaction;

import com.duoshouji.server.service.note.NoteComment;
import com.duoshouji.server.service.user.BasicUser;

public class NoteCommentAndAuthor {

	private NoteComment noteComment;
	private BasicUser author;
	
	public NoteCommentAndAuthor(NoteComment noteComment, BasicUser author) {
		super();
		this.noteComment = noteComment;
		this.author = author;
	}

	public NoteComment getNoteComment() {
		return noteComment;
	}

	public BasicUser getAuthor() {
		return author;
	}
	
}
