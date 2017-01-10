package com.duoshouji.core.note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.duoshouji.core.FullFunctionalNote;
import com.duoshouji.core.FullFunctionalUser;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.NoteComment;
import com.duoshouji.service.note.NoteImage;
import com.duoshouji.service.util.Location;

class OperationDelegatingNote implements FullFunctionalNote {
	
	final NoteRepository operationManager;
	final long noteId;
	
	String title;
	NoteImage mainImage;
	int ownerRating;
	int commentRatingSum;
	int commentCount;
	int transactionCount;
	long publishedTime;
	long authorId;
	String content;
	List<NoteImage> otherImages = new LinkedList<NoteImage>();
	String address;
	Location location;
	List<Long> likers = new LinkedList<Long>();
	
	public OperationDelegatingNote(long noteId, NoteRepository operationManager) {
		this.noteId = noteId;
		this.operationManager = operationManager;
	}
	
	@Override
	public long getNoteId() {
		return noteId;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public NoteImage getMainImage() {
		return mainImage;
	}

	@Override
	public int getRating() {
		if (commentCount == 0) {
			return ownerRating;
		}
		return (commentRatingSum + ownerRating) / (commentCount + 1);
	}

	@Override
	public int getLikeCount() {
		return likers.size();
	}

	@Override
	public int getCommentCount() {
		return commentCount;
	}

	@Override
	public int getTransactionCount() {
		return transactionCount;
	}

	@Override
	public long getPublishedTime() {
		return publishedTime;
	}

	@Override
	public FullFunctionalUser getAuthor() {
		return operationManager.getAuthor(this);
	}

	@Override
	public List<NoteImage> getImages() {
		List<NoteImage> allImages = new ArrayList<NoteImage>(otherImages.size() + 1);
		allImages.addAll(otherImages);
		allImages.add(getMainImage());
		return allImages;
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public void setImages(NoteImage[] images) {
		if (images == null || images.length == 0) {
			return;
		}
		operationManager.setImages(this, images);
		this.mainImage = images[0];
		final NoteImage[] otherImages = Arrays.copyOfRange(images, 1, images.length);
		this.otherImages = Arrays.asList(otherImages);
	}

	@Override
	public List<NoteComment> getComments() {
		return operationManager.getComments(this);
	}

	@Override
	public void addComment(long authorId, CommentPublishAttributes commentAttributes) {
		operationManager.addComment(authorId, commentAttributes, this);
		++commentCount;
	}

	@Override
	public void likedByUser(long userId) {
		operationManager.likedByUser(userId, this);
		likers.add(Long.valueOf(userId));
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public boolean isLikedBy(long userId) {
		return likers.contains(Long.valueOf(userId));
	}

}
