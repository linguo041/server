package com.duoshouji.server.service.note;

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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NoteFilter))
			return false;
		NoteFilter that = (NoteFilter) obj;
		if (isTagSet() == that.isTagSet()) {
			if (isTagSet()) {
				return tag.equals(that.tag);
			} else {
				return true;
			}
		}
		return false;
	}
}
