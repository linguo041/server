package com.duoshouji.core.note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.duoshouji.core.Note;
import com.duoshouji.service.common.Tag;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.NoteComment;
import com.duoshouji.service.util.Image;

class OperationDelegatingNote extends SimpleBasicNote implements Note {

	String content;
	List<Image> otherImages;
	List<Tag> tags;
	String productName;
	
	public OperationDelegatingNote(long noteId, NoteRepository operationManager) {
		super(noteId, operationManager);
	}

	@Override
	public List<Image> getImages() {
		List<Image> allImages = new ArrayList<Image>(otherImages.size() + 1);
		allImages.addAll(otherImages);
		allImages.add(getMainImage());
		return allImages;
	}

	@Override
	public List<Tag> getTags() {
		return Collections.unmodifiableList(tags);
	}

	@Override
	public String getContent() {
		return content;
	}

	@Override
	public String getProductName() {
		return productName;
	}

	@Override
	public void setImages(Image[] images) {
		if (images == null || images.length == 0) {
			return;
		}
		operationManager.setImages(this, images);
		this.mainImage = images[0];
		final Image[] otherImages = Arrays.copyOfRange(images, 1, images.length);
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
		++likeCount;
	}
}
