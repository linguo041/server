package com.duoshouji.server.internal.core;

interface UserNoteInteractionAware {

	void fireAddComment(int rating);
	
	void fireAddLike();
}
