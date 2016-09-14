package com.duoshouji.server.service.note;

import java.util.Objects;

import com.duoshouji.server.service.common.Tag;
import com.duoshouji.server.util.Location;

public abstract class NoteFilter {

	public abstract Tag getTag();
	
	public abstract boolean isTagSet();
	
	public abstract Location getUserLocation();
	
	public abstract boolean isUserLocationSet();
	
	@Override
	public int hashCode() {
		return Objects.hash(getTag(), getUserLocation());
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
		return Objects.equals(getTag(), that.getTag())
				&& Objects.equals(getUserLocation(), that.getUserLocation());
	}
}
