package com.duoshouji.service.note;

import com.duoshouji.service.util.Image;

public interface BasicNote extends AuthorReference {

	long getNoteId();

	String getTitle();
	
	Image getMainImage();
	
	int getRating();

	int getLikeCount();

	int getCommentCount();

	int getTransactionCount();

	long getPublishedTime();
}