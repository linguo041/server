package com.duoshouji.server.service.dao;

import java.util.List;

import com.duoshouji.server.service.note.NoteFilter;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.IndexRange;
import com.duoshouji.server.util.MobileNumber;

public interface UserNoteDao {

	RegisteredUserDto findUser(MobileNumber mobileNumber);
	
	void createUser(MobileNumber mobileNumber);

	void saveUserProfile(MobileNumber mobileNumber, String nickname);

	void savePasswordDigest(MobileNumber mobileNumber, String passwordDigest);

	void savePortrait(MobileNumber mobileNumber, Image portrait);
	
	long createNote(MobileNumber mobileNumber, NotePublishAttributes noteAttributes);

	void saveNoteImage(long noteId, Image noteImage);
	
	NoteDto findNote(long noteId);
	
	List<NoteDto> findNotes(long cutoff, IndexRange range, NoteFilter filter);
	
	List<NoteDto> findNotes(long cutoff, IndexRange range, MobileNumber userId);

	MobileNumber findNoteOwner(long noteId);
}
