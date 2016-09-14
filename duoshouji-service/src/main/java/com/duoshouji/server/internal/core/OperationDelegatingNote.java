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
	List<Image> images;
	List<Tag> tags;
	
	public OperationDelegatingNote(UserNoteOperationManager operationManager, long noteId) {
		super(noteId);
		this.operationManager = operationManager;
	}

	@Override
	public void setMainImage(Image mainImage) {
		operationManager.setMainImage(this, mainImage);
		this.mainImage = mainImage;
	}

	@Override
	public List<Image> getImages() {
		List<Image> allImages = new ArrayList<Image>(images.size() + 1);
		allImages.addAll(images);
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

}
