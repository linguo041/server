package com.duoshouji.server.service.dao;

import java.util.List;

import com.duoshouji.server.service.note.NoteFilter;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.IndexRange;
import com.duoshouji.server.util.MobileNumber;

public interface UserNoteDao {

	RegisteredUserDto findUser(MobileNumber mobileNumber);
	
	List<NoteDto> findNotes(long cutoff, IndexRange range, NoteFilter filter);

	void createUser(MobileNumber mobileNumber);
	
	void removeToken(MobileNumber mobileNumber);

	void saveToken(MobileNumber mobileNumber, String token);

	void saveUserProfile(MobileNumber mobileNumber, String nickname);

	void savePasswordDigest(MobileNumber mobileNumber, String passwordDigest);

	long createNote(MobileNumber mobileNumber, NotePublishAttributes noteAttributes);

	void savePortrait(MobileNumber mobileNumber, Image portrait);

	void saveNoteImage(long noteId, Image noteImage);
}
