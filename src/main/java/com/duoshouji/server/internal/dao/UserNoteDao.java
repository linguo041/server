package com.duoshouji.server.internal.dao;

import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;

public interface UserNoteDao {

	RegisteredUserDto findUser(UserIdentifier userId);

	void addUser(UserIdentifier userId, MobileNumber mobileNumber);

	NoteDtoCollection findNotes();

	NoteAlbumDto findNoteAlbum(NoteDto noteDto);

	RegisteredUserDto getOwner(NoteDto noteDto);

	LikeDtoCollection getLikes(NoteDto noteDto);

	CommentDtoCollection getComments(NoteDto noteDto);
}
