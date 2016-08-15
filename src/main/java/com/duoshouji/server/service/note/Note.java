package com.duoshouji.server.service.note;

import com.duoshouji.server.service.user.RegisteredUser;

public interface Note {

	long getNoteId();
	
	String getTitle();
	
	NoteAlbum getNoteAlbum();
	
	RegisteredUser getOwner();
	
	int getRank();
	
	LikeCollection getLikes();
	
	CommentCollection getComments();
}
