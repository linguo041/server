package com.duoshouji.service.note;

import java.math.BigDecimal;
import java.util.List;

public interface NoteFacade {
	
	public static final long NULL_NOTE_ID = -1;
	
	SquareNoteRequester newSquareNoteRequester();
	
	NoteCollection listPublishedNotes(long authorId, long timestamp);
	
	Note getNote(long noteId);

	List<NoteComment> getNoteComments(long noteId);
	
	NotePublisher newNotePublisher(long userId);
		
	void deleteNote(long noteId);
	
	void publishComment(long userId, long noteId, CommentPublishAttributes commentAttributes);

	void likeNote(long userId, long noteId);
	
	void setNoteImages(long noteId, NoteImage[] images);
	
	public interface SquareNoteRequester {

		void setChannelId(long tagId);
		
		void setFollowedNoteOnly(long followerId);
		
		NoteCollection getSquareNotes(long timestamp);
	}
	
	public interface NotePublisher {

		void setTitle(String title);

		void setContent(String content);

		void setRating(int rating);
		
		void setLocation(BigDecimal longitude, BigDecimal latitude);
		
		void setAddress(String address);
		
		long publishNote();
	}

}
