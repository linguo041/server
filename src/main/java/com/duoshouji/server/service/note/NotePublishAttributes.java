package com.duoshouji.server.service.note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotePublishAttributes {

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
	
	public void addTag(Tag tag) {
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
