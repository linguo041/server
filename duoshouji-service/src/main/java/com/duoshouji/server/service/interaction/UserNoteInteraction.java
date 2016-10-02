package com.duoshouji.server.service.interaction;

import com.duoshouji.server.service.note.BasicNote;
import com.duoshouji.server.service.note.CommentPublishAttributes;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteComment;
import com.duoshouji.server.service.note.NoteFilter;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.service.user.BasicUser;
import com.duoshouji.util.MobileNumber;

public interface UserNoteInteraction {
	
	NoteCollection listSquareNotes(NoteFilter filter, long timestamp, MobileNumber userId);
	
	NoteCollection getUserPublishedNotes(BasicUser user);
	
	long publishNote(BasicUser user, NotePublishAttributes noteAttributes);
	
	BasicUser getOwner(BasicNote note);
	
	void publishComment(long noteId, CommentPublishAttributes commentAttributes, MobileNumber userId);

	void likeNote(long noteId, MobileNumber userId);
	
	BasicUser getAuthor(NoteComment noteComment);
}
