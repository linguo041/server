package com.duoshouji.core;

import java.util.List;

import com.duoshouji.service.annotation.Unique;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.NoteComment;
import com.duoshouji.service.note.NoteDetail;
import com.duoshouji.service.util.Image;

@Unique
public interface Note extends NoteDetail {
	
	@Override
	FullFunctionalUser getAuthor();
	
	List<NoteComment> getComments();

	void addComment(long authorId, CommentPublishAttributes commentAttributes);

	void likedByUser(long userId);

	void setImages(Image[] images);
}
