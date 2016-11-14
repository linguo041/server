package com.duoshouji.core.note;

import java.util.List;

import com.duoshouji.core.FullFunctionalNote;
import com.duoshouji.core.FullFunctionalUser;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.NoteComment;
import com.duoshouji.service.note.NoteImage;
import com.duoshouji.service.util.Location;

public class NoteProxy implements FullFunctionalNote {

	private long noteId;
	private NoteRepository noteRepository;
	private OperationDelegatingNote delegator;
	
	NoteProxy(long noteId, NoteRepository noteRepository) {
		this.noteId = noteId;
		this.noteRepository = noteRepository;
	}
	
	private FullFunctionalNote getNote() {
		if (delegator == null) {
			delegator = noteRepository.loadNote(noteId);
		}
		return delegator;
	}
	
	@Override
	public List<NoteImage> getImages() {
		return getNote().getImages();
	}

	@Override
	public String getContent() {
		return getNote().getContent();
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
	public NoteImage getMainImage() {
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
	public void setImages(NoteImage[] images) {
		getNote().setImages(images);
	}

	@Override
	public String getAddress() {
		return getNote().getAddress();
	}

	@Override
	public Location getLocation() {
		return getNote().getLocation();
	}

}
