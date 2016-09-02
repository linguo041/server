package com.duoshouji.server.service.interaction;

import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.util.MobileNumber;

public interface UserNoteInteraction {
	
	NoteCollection getUserPublishedNotes(MobileNumber userId);
	
	long publishNote(MobileNumber userId, NotePublishAttributes noteAttributes);
}
