package com.duoshouji.core;

import java.util.List;

import com.duoshouji.core.dao.dto.BasicNoteDto;
import com.duoshouji.core.dao.dto.NoteCommentDto;
import com.duoshouji.core.dao.dto.NoteDetailDto;
import com.duoshouji.core.util.IndexRange;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.NotePublishAttributes;
import com.duoshouji.service.util.Image;

public interface NoteDao {
	
	long createNote(long authorId, NotePublishAttributes noteAttributes);

	void saveNoteImages(long noteId, Image[] noteImages);
	
	NoteDetailDto findNote(long noteId);
	
	List<BasicNoteDto> findNotes(long cutoff, IndexRange range, NoteFilter filter, long followerId);
		
	List<BasicNoteDto> findNotes(long cutoff, IndexRange range, long authorId);

	void createComment(long noteId, CommentPublishAttributes commentAttributes, long authorId);

	void saveUserLikeNote(long noteId, long userId);
	
	List<NoteCommentDto> getNoteComments(long noteId);
}
