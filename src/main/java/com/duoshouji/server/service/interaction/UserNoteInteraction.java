package com.duoshouji.server.service.interaction;

import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.util.MobileNumber;

public interface UserNoteInteraction {
	
	long publishNote(MobileNumber userId, NotePublishAttributes noteAttributes);

	NoteCollection getUserPublishedNotes(MobileNumber userId);
	
	NoteCollection getPushedNotes(MobileNumber userId, NoteFilter filter);
}
