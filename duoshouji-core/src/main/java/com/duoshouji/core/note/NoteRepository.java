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

import com.duoshouji.core.FullFunctionalNote;
import com.duoshouji.core.FullFunctionalUser;
import com.duoshouji.core.NoteDao;
import com.duoshouji.core.NoteFilter;
import com.duoshouji.core.dao.dto.BasicNoteDto;
import com.duoshouji.core.dao.dto.NoteCommentDto;
import com.duoshouji.core.dao.dto.NoteDetailDto;
import com.duoshouji.core.user.UserRepository;
import com.duoshouji.core.util.IndexRange;
import com.duoshouji.service.common.BrandRepository;
import com.duoshouji.service.common.CategoryRepository;
import com.duoshouji.service.common.DistrictRepository;
import com.duoshouji.service.note.CommentPublishAttributes;
import com.duoshouji.service.note.Note;
import com.duoshouji.service.note.NoteCollection;
import com.duoshouji.service.note.NoteComment;
import com.duoshouji.service.note.NoteImage;
import com.duoshouji.service.note.NotePublishAttributes;
import com.duoshouji.service.user.BasicUser;

@Service
class NoteRepository {

	private NoteDao noteDao;
	private UserRepository userRepository;
	private BrandRepository brandRepository;
	private CategoryRepository categoryRepository;
	private DistrictRepository districtRepository;
	private Map<Long, FullFunctionalNote> noteCache = new HashMap<Long, FullFunctionalNote>();
	
	@Required
	@Autowired
	public void setNoteDao(NoteDao noteDao) {
		this.noteDao = noteDao;
	}

	@Required
	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Required
	@Autowired
	public void setDistrictRepository(DistrictRepository districtRepository) {
		this.districtRepository = districtRepository;
	}

	@Required
	@Autowired
	public void setBrandRepository(BrandRepository brandRepository) {
		this.brandRepository = brandRepository;
	}

	@Required
	@Autowired
	public void setCategoryRepository(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
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
		
		if (noteDto.brandId > 0) {
			note.brand = brandRepository.findBrand(noteDto.brandId);
		}
		if (noteDto.categoryId > 0) {
			note.category = categoryRepository.findCategory(noteDto.categoryId);
		}
		note.productName = noteDto.productName;
		note.price = noteDto.price;
		if (noteDto.districtId > 0) {
			note.district = districtRepository.findDistrict(noteDto.districtId);
		}
	}
	
	public FullFunctionalNote getNote(long noteId) {
		FullFunctionalNote note = noteCache.get(Long.valueOf(noteId));
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

	public FullFunctionalNote createNote(long authorId, NotePublishAttributes notePublishAttributes) {
		final long noteId = noteDao.createNote(authorId, notePublishAttributes);
		return getNote(noteId);
	}

	Iterator<Note> findNotes(long cutoff, IndexRange range, long authorId) {
		return new InnerNoteIterator(noteDao.findNotes(cutoff, range, authorId));
	}

	Iterator<Note> findNotes(long cutoff, IndexRange range, NoteFilter noteFilter, long followerId) {
		return new InnerNoteIterator(noteDao.findNotes(cutoff, range, noteFilter, followerId));
	}
	
	List<NoteComment> getComments(OperationDelegatingNote note) {
		List<NoteComment> results = new LinkedList<NoteComment>();
		for (NoteCommentDto commentDto : noteDao.getNoteComments(note.getNoteId())) {
			results.add(new InnerNoteComment(commentDto.authorId, commentDto.comment));
		}
		return results;
	}

	FullFunctionalUser getAuthor(OperationDelegatingNote note) {
		return userRepository.getUser(note.authorId);
	}
	
	void setImages(OperationDelegatingNote note, NoteImage[] images) {
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
	
	private class InnerNoteIterator implements Iterator<Note> {
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
		public Note next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return getNote(noteDtos.get(index++).noteId);
		}
	}

}
