package com.duoshouji.server.service.note;

import com.duoshouji.server.service.user.RegisteredUser;

public interface NoteFacade {

	NoteCollection pushSquareNotes(RegisteredUser user);

	NoteCollection getPushedSquareNotes(RegisteredUser user);
}
