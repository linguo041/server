package com.duoshouji.server.service.note;

import com.duoshouji.server.util.Image;

public interface BasicNote {

	long getNoteId();

	String getTitle();
	
	Image getMainImage();
	
	int getRating();

	int getLikeCount();

	int getCommentCount();

	int getTransactionCount();

	long getPublishedTime();
}