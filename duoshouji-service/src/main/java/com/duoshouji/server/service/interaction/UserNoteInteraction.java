package com.duoshouji.server.service.interaction;

import com.duoshouji.server.service.note.BasicNote;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.service.user.BasicUser;

public interface UserNoteInteraction {
	
	NoteCollection getUserPublishedNotes(BasicUser user);
	
	long publishNote(BasicUser user, NotePublishAttributes noteAttributes);
	
	BasicUser getOwner(BasicNote note);
	
}
