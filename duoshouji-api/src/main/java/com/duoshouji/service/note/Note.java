package com.duoshouji.service.note;

import java.util.List;

public interface Note extends AuthorReference, NoteTextProperties {

	long getNoteId();
	
	int getRating();

	int getLikeCount();

	int getCommentCount();

	int getTransactionCount();

	long getPublishedTime();
	
	List<NoteImage> getImages();
	
	NoteImage getMainImage();

}