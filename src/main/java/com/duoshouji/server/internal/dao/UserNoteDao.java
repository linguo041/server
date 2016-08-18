package com.duoshouji.server.internal.dao;

import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;

public interface UserNoteDao {

	RegisteredUserDto findUser(UserIdentifier userId);

	void addUser(UserIdentifier userId, MobileNumber mobileNumber);

	NoteAlbumDto findNoteAlbum(NoteDto noteDto);

	RegisteredUserDto findOwner(NoteDto noteDto);

	LikeDtoCollection findLikes(NoteDto noteDto);

	CommentDtoCollection findComments(NoteDto noteDto);
	
	NoteDtoCollection findNotes(long cutoff);

	NoteDtoCollection findNotes(long cutoff, int startIndex, int endIndex);
}
