package com.duoshouji.server.service.note;

import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.util.Location;




public interface NoteFacade {

	public static interface PushedNoteRequestor {
		
		NoteCollection getPushedNotes();

		void setUserMarked(RegisteredUser user);

		void setLocation(Location location);

		void setCategory(int categoryId);
	}

	PushedNoteRequestor getPushedNoteRequestor();
}
