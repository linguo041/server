package com.duoshouji.server.service.note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.duoshouji.server.service.common.Tag;

public class NotePublishAttributes {

	private static final int MAX_TAG_COUNT = 9;
	
	private String title;
	private String content;
	private List<Tag> tags = new ArrayList<Tag>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Tag> getTags() {
		return Collections.unmodifiableList(tags);
	}
	
	public int getTagCount() {
		return tags.size();
	}
	
	public void addTag(Tag tag) {
		if (tags.size() >= MAX_TAG_COUNT) {
			throw new NotePublishException("Tag count has exceeds maximum value; maximum allowed tag count: 9");
		}
		tags.add(tag);
	}
	
	public void checkAttributesSetup() {
		if (title == null
				|| content == null
				|| tags.isEmpty()) {
			throw new NotePublishException("Title, content, tags are all required");
		}
	}
}
