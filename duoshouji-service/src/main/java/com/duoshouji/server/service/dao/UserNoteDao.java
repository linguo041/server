package com.duoshouji.server.service.dao;

import java.util.List;

import com.duoshouji.server.service.note.CommentPublishAttributes;
import com.duoshouji.server.service.note.NoteFilter;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.service.user.Gender;
import com.duoshouji.server.util.IndexRange;
import com.duoshouji.util.Image;
import com.duoshouji.util.MobileNumber;

public interface UserNoteDao {

	RegisteredUserDto findUser(MobileNumber mobileNumber);
	
	void createUser(MobileNumber mobileNumber);

	void saveNickname(MobileNumber mobileNumber, String nickname);

	void saveGender(MobileNumber mobileNumber, Gender gender);
	
	void savePasswordDigest(MobileNumber mobileNumber, String passwordDigest);

	void savePortrait(MobileNumber mobileNumber, Image portrait);
	
	long createNote(MobileNumber mobileNumber, NotePublishAttributes noteAttributes);

	void saveNoteImages(long noteId, Image[] noteImages);
	
	NoteDetailDto findNote(long noteId);
	
	List<BasicNoteDto> findNotes(long cutoff, IndexRange range, NoteFilter filter, MobileNumber userId);
	
	List<BasicNoteDto> findNotes(long cutoff, IndexRange range, MobileNumber userId);

	MobileNumber findNoteOwner(long noteId);

	void createComment(long noteId, CommentPublishAttributes commentAttributes, MobileNumber userId);

	void saveUserLikeNote(long noteId, MobileNumber userId);
	
	void insertFollowConnection(MobileNumber follerId, MobileNumber[] followedIds);
	
	void insertFollowConnection(MobileNumber follerId, MobileNumber[] followedIds, boolean isActivated);

	void activateFollows(MobileNumber userId);

	List<MobileNumber> findFollowerIds(MobileNumber followedId);

	List<NoteCommentDto> getNoteComments(long noteId);
}
