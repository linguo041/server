package com.duoshouji.server.internal.core;

import java.util.List;

import com.duoshouji.server.service.common.Tag;
import com.duoshouji.server.service.note.BasicNote;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteDetail;
import com.duoshouji.server.util.Image;

abstract class AbstractNoteProxy implements Note, UserNoteInteractionAware {

	protected abstract BasicNote getBasicNote();
	
	protected abstract NoteDetail getNoteDetail();
	
	protected abstract Note getDelegator();
	
	@Override
	public final String getTitle() {
		return getBasicNote().getTitle();
	}

	@Override
	public final Image getMainImage() {
		return getBasicNote().getMainImage();
	}

	@Override
	public final int getRating() {
		return getBasicNote().getRating();
	}

	@Override
	public final int getLikeCount() {
		return getBasicNote().getLikeCount();
	}

	@Override
	public final int getCommentCount() {
		return getBasicNote().getCommentCount();
	}

	@Override
	public final int getTransactionCount() {
		return getBasicNote().getTransactionCount();
	}

	@Override
	public final long getPublishedTime() {
		return getBasicNote().getPublishedTime();
	}

	@Override
	public final List<Image> getImages() {
		return getNoteDetail().getImages();
	}

	@Override
	public final List<Tag> getTags() {
		return getNoteDetail().getTags();
	}

	@Override
	public final String getContent() {
		return getNoteDetail().getContent();
	}

	@Override
	public final void setImages(Image[] images) {
		getDelegator().setImages(images);
	}

	@Override
	public String getProductName() {
		return getDelegator().getProductName();
	}
}

