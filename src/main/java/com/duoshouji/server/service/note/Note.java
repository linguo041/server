package com.duoshouji.server.service.note;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.util.Image;

public interface Note {

	long getNoteId();
	
	String getTitle();
	
	Image getMainImage();
	
	RegisteredUser getOwner();
	
	int getRank();
	
	int getLikeCount();
	
	int getCommentCount();

	int getTransactionCount();
	
	long getPublishedTime();

}
