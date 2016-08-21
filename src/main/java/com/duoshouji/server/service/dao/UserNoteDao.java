package com.duoshouji.server.service.dao;

import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MobileNumber;

public interface UserNoteDao {

	RegisteredUserDto findUser(MobileNumber mobileNumber);

	void addUser(MobileNumber mobileNumber);

	NoteAlbumDto findNoteAlbum(NoteDto noteDto);

	RegisteredUserDto findOwner(NoteDto noteDto);

	LikeDtoCollection findLikes(NoteDto noteDto);

	CommentDtoCollection findComments(NoteDto noteDto);
	
	NoteDtoCollection findNotes(long cutoff);

	NoteDtoCollection findNotes(long cutoff, int startIndex, int endIndex);

	RegisteredUserDto findUser(String token);

	void removeToken(UserIdentifier userId);

	void saveToken(UserIdentifier userId, String token);

	void saveUserProfile(UserIdentifier userId, String nickname);
}
