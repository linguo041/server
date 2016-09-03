package com.duoshouji.server.service.note;

import com.duoshouji.server.service.user.BasicUserAttributes;
import com.duoshouji.server.util.Image;

public interface Note {

	long getNoteId();
	
	String getTitle();
	
	Image getMainImage();
	
	BasicUserAttributes getOwner();
	
	int getRank();
	
	int getLikeCount();
	
	int getCommentCount();

	int getTransactionCount();
	
	long getPublishedTime();

}
