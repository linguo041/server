package com.duoshouji.server.service.note;

import java.util.Objects;

import com.duoshouji.server.service.common.Tag;

public class NoteFilter {

	private Tag tag;
	
	public NoteFilter() {
		super();
	}
	
	public void setTag(Tag tag) {
		this.tag = tag;
	}
	
	public Tag getTag() {
		return tag;
	}
	
	public boolean isTagSet() {
		return tag != null;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(tag);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NoteFilter))
			return false;
		NoteFilter that = (NoteFilter) obj;
		return Objects.equals(tag, that.tag);
	}
}
