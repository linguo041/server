package com.duoshouji.server.service.note;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.util.Location;

public interface FilteredNoteCollection extends NoteCollection {

	FilteredNoteCollection filter(RegisteredUser user);

	FilteredNoteCollection filter(Location location);

	FilteredNoteCollection filter(int categoryId);
}
