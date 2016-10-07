package com.duoshouji.core.note;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import com.duoshouji.core.FullFunctionalUser;
import com.duoshouji.core.Note;
import com.duoshouji.core.NoteDao;
import com.duoshouji.core.NoteFilter;
import com.duoshouji.core.dao.dto.BasicNoteDto;
import com.duoshouji.core.dao.dto.NoteCommentDto;
import com.duoshouji.core.dao.dto.NoteDetailDto;
import com.duoshouji.core.user.UserRepository;
import com.duoshouji.core.util.IndexRange;
import com.duoshouji.service.common.TagRepository;
import com.duoshouji.service.note.BasicNote;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.NoteCollection;
import com.duoshouji.service.note.NoteComment;
import com.duoshouji.service.note.NotePublishAttributes;
import com.duoshouji.service.user.BasicUser;
import com.duoshouji.service.util.Image;

@Service
class NoteRepository {

	private NoteDao noteDao;
	private TagRepository tagRepository;
	private UserRepository userRepository;
	private Map<Long, Note> noteCache = new HashMap<Long, Note>();
	
	@Required
	@Autowired
	public void setNoteDao(NoteDao noteDao) {
		this.noteDao = noteDao;
	}

	@Required
	@Autowired
	public void setTagRepository(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	@Required
	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	OperationDelegatingNote loadNote(long noteId) {
		final OperationDelegatingNote note = new OperationDelegatingNote(noteId, this);
		convert(note, noteDao.findNote(noteId));
		return note;
	}
	
	private void convert(OperationDelegatingNote note, NoteDetailDto noteDto) {
		note.commentCount = noteDto.commentCount;
		note.likeCount = noteDto.likeCount;
		note.mainImage = noteDto.mainImage;
		note.publishedTime = noteDto.publishedTime;
		note.commentRatingSum = noteDto.commentRatingSum;
		note.ownerRating = noteDto.ownerRating;
		note.title = noteDto.title;
		note.transactionCount = noteDto.transactionCount;
		note.authorId = noteDto.authorId;
		note.content = noteDto.content;
		note.otherImages = noteDto.images;
		note.tags = tagRepository.findTags(noteDto.tagIds);
		note.productName = noteDto.productName;
	}
	
	public Note getNote(long noteId) {
		Note note = noteCache.get(Long.valueOf(noteId));
		if (note == null) {
			note = new NoteProxy(noteId, this);
			noteCache.put(Long.valueOf(noteId), note);
		}
		return note;
	}

	public NoteCollection listPublishedNotes(long authorId, long timestamp) {
		return new UserPublishedNoteCollection(this, timestamp, authorId);
	}

	public NoteCollection listSquareNotes(NoteFilter noteFilter, long followerId, long timestamp) {
		return new FilteredNoteCollection(this, timestamp, noteFilter, followerId);
	}

	public Note createNote(long authorId, NotePublishAttributes notePublishAttributes) {
		final long noteId = noteDao.createNote(authorId, notePublishAttributes);
		return getNote(noteId);
	}

	Iterator<BasicNote> findNotes(long cutoff, IndexRange range, long authorId) {
		return new InnerNoteIterator(noteDao.findNotes(cutoff, range, authorId));
	}

	Iterator<BasicNote> findNotes(long cutoff, IndexRange range, NoteFilter noteFilter, long followerId) {
		return new InnerNoteIterator(noteDao.findNotes(cutoff, range, noteFilter, followerId));
	}
	
	List<NoteComment> getComments(OperationDelegatingNote note) {
		List<NoteComment> results = new LinkedList<NoteComment>();
		for (NoteCommentDto commentDto : noteDao.getNoteComments(note.getNoteId())) {
			results.add(new InnerNoteComment(commentDto.authorId, commentDto.comment));
		}
		return results;
	}

	FullFunctionalUser getAuthor(SimpleBasicNote note) {
		return userRepository.getUser(note.authorId);
	}
	
	void setImages(OperationDelegatingNote note, Image[] images) {
		noteDao.saveNoteImages(note.getNoteId(), images);
	}

	void addComment(long authorId, CommentPublishAttributes commentAttributes, OperationDelegatingNote note) {
		noteDao.createComment(note.getNoteId(), commentAttributes, authorId);	
	}

	void likedByUser(long userId, OperationDelegatingNote note) {
		noteDao.saveUserLikeNote(note.getNoteId(), userId);
	}

	private class InnerNoteComment implements NoteComment {

		private long authorId;
		private String comment;
		
		private InnerNoteComment(long authorId, String comment) {
			super();
			this.authorId = authorId;
			this.comment = comment;
		}

		@Override
		public BasicUser getAuthor() {
			return userRepository.getUser(authorId);
		}

		@Override
		public String getComment() {
			return comment;
		}
		
	}
	
	private class InnerNoteIterator implements Iterator<BasicNote> {
		private List<BasicNoteDto> noteDtos;
		private int index = 0;
		
		InnerNoteIterator(List<BasicNoteDto> noteDtos) {
			this.noteDtos = noteDtos;
		}
		
		@Override
		public boolean hasNext() {
			return index < noteDtos.size();
		}
		
		@Override
		public BasicNote next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return getNote(noteDtos.get(index++).noteId);
		}
	}

}
