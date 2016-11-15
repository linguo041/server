package com.duoshouji.core.note;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import com.duoshouji.core.FullFunctionalNote;
import com.duoshouji.core.NoteFilter;
import com.duoshouji.service.common.Tag;
import com.duoshouji.service.common.TagRepository;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.Note;
import com.duoshouji.service.note.NoteCollection;
import com.duoshouji.service.note.NoteComment;
import com.duoshouji.service.note.NoteFacade;
import com.duoshouji.service.note.NoteImage;
import com.duoshouji.service.note.NotePublishAttributes;
import com.duoshouji.service.user.UserFacade;
import com.duoshouji.service.util.Location;

@Service
public class NoteFacadeImpl implements NoteFacade {

	private NoteRepository noteRepository;
	private TagRepository tagRepository;

	@Required
	@Autowired
	public void setNoteRepository(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}

	@Required
	@Autowired
	public void setTagRepository(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	@Override
	public SquareNoteRequester newSquareNoteRequester() {
		return new InnerSquareNoteRequester();
	}

	@Override
	public NoteCollection listPublishedNotes(long authorId, long timestamp) {
		return noteRepository.listPublishedNotes(authorId, timestamp);
	}

	@Override
	public Note getNote(long noteId) {
		return noteRepository.getNote(noteId);
	}

	@Override
	public List<NoteComment> getNoteComments(long noteId) {
		return noteRepository.getNote(noteId).getComments();
	}

	@Override
	public NotePublisher newNotePublisher(long userId) {
		return new InnerNoteBuilder(userId);
	}

	@Override
	public void publishComment(long userId, long noteId, CommentPublishAttributes commentAttributes) {
		noteRepository.getNote(noteId).addComment(userId, commentAttributes);
	}

	@Override
	public void likeNote(long userId, long noteId) {
		noteRepository.getNote(noteId).likedByUser(userId);
	}

	@Override
	public void setNoteImages(long noteId, NoteImage[] images) {
		noteRepository.getNote(noteId).setImages(images);
	}
	
	
	private class InnerSquareNoteRequester implements SquareNoteRequester, NoteFilter {
		private Tag tag;
		private long followerId = UserFacade.NULL_USER_ID;
		
		private InnerSquareNoteRequester() {
			super();
		}
		
		@Override
		public void setChannelId(long tagId) {
			tag = tagRepository.findChannel(tagId);
		}

		@Override
		public void setFollowedNoteOnly(long followerId) {
			this.followerId = followerId;
		}

		@Override
		public NoteCollection getSquareNotes(long timestamp) {
			return noteRepository.listSquareNotes(this, followerId, timestamp);
		}
		
		@Override
		public Tag getChannel() {
			return tag;
		}
		
		@Override
		public boolean isChannelSet() {
			return tag != null;
		}
	}
	
	private class InnerNoteBuilder implements NotePublisher, NotePublishAttributes {
		
		private final long userId;
		
		private String title;
		private String content;
		private int rating;
		private Location location;
		private String address;
		
		private InnerNoteBuilder(long userId) {
			this.userId = userId;
		}

		@Override
		public void setTitle(String title) {
			this.title = title;
		}

		@Override
		public void setContent(String content) {
			this.content = content;
		}
		
		@Override
		public void setRating(int rating) {
			this.rating = rating;
		}
		
		@Override
		public void setLocation(BigDecimal longitude, BigDecimal latitude) {
			location = new Location(longitude, latitude);
		}

		@Override
		public void setAddress(String address) {
			this.address = address;
		}

		@Override
		public long publishNote() {
			final FullFunctionalNote note = noteRepository.createNote(userId, this);
			note.getAuthor().firePublishNote();
			return note.getNoteId();
		}

		@Override
		public int getRating() {
			return rating;
		}

		@Override
		public String getTitle() {
			return title;
		}

		@Override
		public String getContent() {
			return content;
		}

		@Override
		public boolean isAddressSet() {
			return address != null;
		}

		@Override
		public String getAddress() {
			return address;
		}

		@Override
		public boolean isLocationSet() {
			return location != null;
		}

		@Override
		public Location getLocation() {
			return location;
		}
	}
}
