package com.duoshouji.service.note;

import java.util.List;

import com.duoshouji.service.util.Image;

public interface Note extends AuthorReference, NoteTextProperties {

	long getNoteId();
	
	int getRating();

	int getLikeCount();

	int getCommentCount();

	int getTransactionCount();

	long getPublishedTime();
	
	List<Image> getImages();
	
	Image getMainImage();

}