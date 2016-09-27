package com.duoshouji.server.internal.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.duoshouji.server.service.common.Tag;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.util.Image;

class OperationDelegatingNote extends InMemoryBasicNote implements Note {

	private UserNoteOperationManager operationManager;
	String content;
	List<Image> otherImages;
	List<Tag> tags;
	String productName;
	
	public OperationDelegatingNote(UserNoteOperationManager operationManager, long noteId) {
		super(noteId);
		this.operationManager = operationManager;
	}

	@Override
	public void setImages(Image[] images) {
		if (images.length == 0) {
			return;
		}
		operationManager.setImages(this, images);
		this.mainImage = images[0];
		for (int i = 1; i < images.length; ++i) {
			otherImages.add(images[i]);
		}
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
}
