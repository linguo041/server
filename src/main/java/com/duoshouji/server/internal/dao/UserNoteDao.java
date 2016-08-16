package com.duoshouji.server.internal.dao;

import com.duoshouji.server.internal.note.NoteCollectionDto;
import com.duoshouji.server.internal.user.RegisteredUserDto;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;

public interface UserNoteDao {

	RegisteredUserDto findUser(UserIdentifier userId);

	void addUser(UserIdentifier userId, MobileNumber mobileNumber);

	NoteCollectionDto findNotes();
}
