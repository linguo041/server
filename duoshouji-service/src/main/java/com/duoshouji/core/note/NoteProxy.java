package com.duoshouji.core.note;

import java.util.List;

import com.duoshouji.core.FullFunctionalUser;
import com.duoshouji.core.Note;
import com.duoshouji.service.common.Tag;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.NoteComment;
import com.duoshouji.service.util.Image;

public class NoteProxy implements Note {

	private long noteId;
	private NoteRepository noteRepository;
	private OperationDelegatingNote delegator;
	
	NoteProxy(long noteId, NoteRepository noteRepository) {
		this.noteId = noteId;
		this.noteRepository = noteRepository;
	}
	
	private Note getNote() {
		if (delegator == null) {
			delegator = noteRepository.loadNote(noteId);
		}
		return delegator;
	}
	
	@Override
	public List<Image> getImages() {
		return getNote().getImages();
	}

	@Override
	public List<Tag> getTags() {
		return getNote().getTags();
	}

	@Override
	public String getContent() {
		return getNote().getContent();
	}

	@Override
	public String getProductName() {
		return getNote().getProductName();
	}

	@Override
	public long getNoteId() {
		return noteId;
	}

	@Override
	public String getTitle() {
		return getNote().getTitle();
	}

	@Override
	public Image getMainImage() {
		return getNote().getMainImage();
	}

	@Override
	public int getRating() {
		return getNote().getRating();
	}

	@Override
	public int getLikeCount() {
		return getNote().getLikeCount();
	}

	@Override
	public int getCommentCount() {
		return getNote().getCommentCount();
	}

	@Override
	public int getTransactionCount() {
		return getNote().getTransactionCount();
	}

	@Override
	public long getPublishedTime() {
		return getNote().getPublishedTime();
	}

	@Override
	public FullFunctionalUser getAuthor() {
		return getNote().getAuthor();
	}

	@Override
	public List<NoteComment> getComments() {
		return getNote().getComments();
	}

	@Override
	public void addComment(long authorId, CommentPublishAttributes commentAttributes) {
		getNote().addComment(authorId, commentAttributes);
	}

	@Override
	public void likedByUser(long userId) {
		getNote().likedByUser(userId);
	}

	@Override
	public void setImages(Image[] images) {
		getNote().setImages(images);
	}

}
