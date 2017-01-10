package com.duoshouji.service.common;

import java.util.List;

import com.duoshouji.service.note.NoteTextProperties;

public abstract class TagRepository {

	public abstract List<Tag> getChannels();

	public abstract Tag findChannel(long tagId);
	
	public abstract List<Tag> findTags(NoteTextProperties noteProperties);

	protected final Tag createTag(long tagId, String tagName) {
		return new Tag(tagId, tagName);
	}
}

