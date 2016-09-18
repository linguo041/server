package com.duoshouji.server.internal.core;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.common.TagRepository;
import com.duoshouji.server.service.dao.BasicNoteDto;
import com.duoshouji.server.service.dao.BasicUserDto;
import com.duoshouji.server.service.dao.NoteDetailDto;
import com.duoshouji.server.service.dao.RegisteredUserDto;
import com.duoshouji.server.service.dao.UserNoteDao;
import com.duoshouji.server.service.interaction.UserNoteInteraction;
import com.duoshouji.server.service.note.BasicNote;
import com.duoshouji.server.service.note.CommentPublishAttributes;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteFilter;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.user.BasicUser;
import com.duoshouji.server.service.user.FullFunctionalUser;
import com.duoshouji.server.service.user.Gender;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.IndexRange;
import com.duoshouji.server.util.MessageProxyFactory;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.UserMessageProxy;
import com.google.common.base.MoreObjects;

@Service
public class UserNoteOperationManager implements UserRepository, NoteRepository, UserNoteInteraction {

	private UserCache userCache = new UserCache();
	private NoteCache noteCache = new NoteCache();
	private UserNoteDao userNoteDao;
	private MessageProxyFactory messageProxyFactory;
	private TagRepository tagRepository;

	private final class UserCache {
		
		private UniqueObjectCache cache = new HashMapUniqueObjectCache();
		
		private OperationDelegatingMobileUser getUserIfLoaded(MobileNumber mobileNumber) {
			FullFunctionalUser user = cache.get(mobileNumber, FullFunctionalUser.class);
			if (user instanceof OperationDelegatingMobileUser) {
				return (OperationDelegatingMobileUser) user;
			}
			return null;
		}
		
		private FullFunctionalUser getUser(MobileNumber mobileNumber) {
			FullFunctionalUser user = cache.get(mobileNumber, FullFunctionalUser.class);
			if (user == null) {
				user = new MobileNumberUserProxy(mobileNumber, UserNoteOperationManager.this);
				cache.put(mobileNumber, user);
			}
			return user;
		}
		
		private FullFunctionalUser getUser(BasicUserDto basicUserDto) {
			FullFunctionalUser user = cache.get(basicUserDto.mobileNumber, FullFunctionalUser.class);
			if (user instanceof MobileNumberUserProxy) {
				InMemoryBasicUser userAttributes = new InMemoryBasicUser(basicUserDto.mobileNumber);
				userAttributes.nickname = basicUserDto.nickname;
				userAttributes.portrait = basicUserDto.portrait;
				userAttributes.gender = basicUserDto.gender;
				user = new BasicUserProxy(userAttributes, UserNoteOperationManager.this);
				cache.put(user.getMobileNumber(), user);
			}
			return user;

		}
		
		private FullFunctionalUser getUser(RegisteredUserDto userDto) {
			FullFunctionalUser user = cache.get(userDto.mobileNumber, FullFunctionalUser.class);
			if (!(user instanceof OperationDelegatingMobileUser)) {
				OperationDelegatingMobileUser tempUser = new OperationDelegatingMobileUser(userDto.mobileNumber, UserNoteOperationManager.this);
				tempUser.passwordDigest = userDto.passwordDigest;
				tempUser.nickname = userDto.nickname;
				tempUser.portrait = userDto.portrait;
				tempUser.gender = userDto.gender;
				tempUser.totalRevenue = MoreObjects.firstNonNull(userDto.totalRevenue, BigDecimal.ZERO);
				tempUser.publishedNoteCount = userDto.publishedNoteCount;
				tempUser.transactionCount = userDto.transactionCount;
				tempUser.watchCount = userDto.watchCount;
				tempUser.fanCount = userDto.fanCount;
				cache.put(user.getMobileNumber(), tempUser);
				user = tempUser;
			}
			return user;
		}
	}
	
	private final class NoteCache {
		
		private UniqueObjectCache cache = new HashMapUniqueObjectCache();
		
		private Note getNote(long noteId) {
			return cache.get(Long.valueOf(noteId), Note.class);
		}
		
		private Note getNote(BasicNoteDto noteDto) {
			Note note = cache.get(Long.valueOf(noteDto.noteId), Note.class);
			if (note == null) {
				InMemoryBasicNote delegator = new InMemoryBasicNote(noteDto.noteId);
				mapDto2BasicNote(delegator, noteDto);
				note = new NoteProxyWithBasicNote(UserNoteOperationManager.this, delegator);
				cache.put(Long.valueOf(noteDto.noteId), note);
			}
			return note;
		}
		
		private OperationDelegatingNote getNote(NoteDetailDto noteDto) {
			Note note = cache.get(Long.valueOf(noteDto.noteId), Note.class);
			if (note == null || !(note instanceof OperationDelegatingNote)) {
				OperationDelegatingNote tempNote = new OperationDelegatingNote(UserNoteOperationManager.this, noteDto.noteId);
				mapDto2BasicNote(tempNote, noteDto);
				tempNote.content = noteDto.content;
				tempNote.otherImages = noteDto.images;
				tempNote.tags = tagRepository.findTags(noteDto.tagIds);
				cache.put(Long.valueOf(noteDto.noteId), tempNote);
				note = tempNote;
			}
			return (OperationDelegatingNote) note;
		}
		
		private OperationDelegatingNote getNoteIfLoaded(long noteId) {
			Note note = cache.get(Long.valueOf(noteId), Note.class);
			if (note != null && (note instanceof OperationDelegatingNote)) {
				return (OperationDelegatingNote) note;
			}
			return null;
		}
		
		private void mapDto2BasicNote(InMemoryBasicNote note, BasicNoteDto noteDto) {
			note.commentCount = noteDto.commentCount;
			note.likeCount = noteDto.likeCount;
			note.mainImage = noteDto.mainImage;
			note.publishedTime = noteDto.publishedTime;
			note.commentRatingSum = noteDto.commentRatingSum;
			note.ownerRating = noteDto.ownerRating;
			note.title = noteDto.title;
			note.transactionCount = noteDto.transactionCount;
			note.owner = userCache.getUser(noteDto.owner);
		}
	}
	
	@Autowired
	@Required
	public void setUserNoteDao(UserNoteDao userNoteDao) {
		this.userNoteDao = userNoteDao;
	}

	@Autowired
	@Required
	public void setMessageProxyFactory(MessageProxyFactory messageProxyFactory) {
		this.messageProxyFactory = messageProxyFactory;
	}
	
	@Autowired
	@Required
	public void setTagRepository(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}

	OperationDelegatingMobileUser loadUserIfNotExists(MobileNumber mobileNumber) {
		FullFunctionalUser user = userCache.getUser(mobileNumber);
		if (!(user instanceof OperationDelegatingMobileUser)) {
			user = userCache.getUser(loadUserFromDao(mobileNumber));
		}
		return (OperationDelegatingMobileUser) user;
	}
	
	private RegisteredUserDto loadUserFromDao(MobileNumber mobileNumber) {
		RegisteredUserDto userDto = userNoteDao.findUser(mobileNumber);
		if (userDto == null) {
			userNoteDao.createUser(mobileNumber);
			userDto = new RegisteredUserDto();
			userDto.mobileNumber = mobileNumber;
		}
		return userDto;		
	}
	
	@Override
	public FullFunctionalUser findUser(MobileNumber mobileNumber) {
		return userCache.getUser(mobileNumber);
	}
	
	@Override
	public NoteCollection listSquareNotes(NoteFilter noteFilter) {
		return new FilteredNoteCollection(this, System.currentTimeMillis(), noteFilter);
	}

	@Override
	public NoteCollection listSquareNotes(NoteFilter noteFilter, MobileNumber userId) {
		return new FilteredNoteCollection(this, System.currentTimeMillis(), noteFilter, userId);
	}

	Iterator<BasicNote> findNotes(long cutoff, IndexRange range, NoteFilter filter, MobileNumber userId) {
		return new InnerNoteIterator(userNoteDao.findNotes(cutoff, range, filter, userId));
	}
	
	Iterator<BasicNote> findNotes(long cutoff, IndexRange range, MobileNumber userId) {
		return new InnerNoteIterator(userNoteDao.findNotes(cutoff, range, userId));
	}	
	
	UserMessageProxy getMessageProxy(OperationDelegatingMobileUser user) {
		return messageProxyFactory.getMessageProxy(user);
	}

	boolean verifyPassword(OperationDelegatingMobileUser user, Password password) {
		return user.passwordDigest.equals(password.toString());
	}
	
	void setNickname(OperationDelegatingMobileUser user, String nickname) {
		userNoteDao.saveNickname(user.getMobileNumber(), nickname);
	}

	void setPassword(OperationDelegatingMobileUser user, Password password) {
		userNoteDao.savePasswordDigest(user.getMobileNumber(), password.toString());
	}

	void setPortrait(OperationDelegatingMobileUser user, Image portrait) {
		userNoteDao.savePortrait(user.getMobileNumber(), portrait);
	}

	void setImages(OperationDelegatingNote note, Image[] images) {
		userNoteDao.saveNoteImages(note.getNoteId(), images);
	}
	
	public void setGender(OperationDelegatingMobileUser user, Gender gender) {
		userNoteDao.saveGender(user.getMobileNumber(), gender);
	}
	
	@Override
	public NoteCollection getUserPublishedNotes(BasicUser user) {
		return new UserPublishedNoteCollection(this, System.currentTimeMillis(), user.getMobileNumber());
	}
	
	@Override
	public long publishNote(BasicUser user, NotePublishAttributes noteAttributes) {
		final long noteId = userNoteDao.createNote(user.getMobileNumber(), noteAttributes);
		OperationDelegatingMobileUser loadedUser = null;
		if (user instanceof OperationDelegatingMobileUser) {
			loadedUser = (OperationDelegatingMobileUser) user;
		} else {
			loadedUser = userCache.getUserIfLoaded(user.getMobileNumber());
		}
		if (loadedUser != null) {
			loadedUser.publishedNoteCount++;
		}
		return noteId;
	}
	
	@Override
	public BasicUser getOwner(BasicNote note) {
		BasicUser user = null;
		if (note instanceof InMemoryBasicNote) {
			user = ((InMemoryBasicNote) note).owner;
		} else if (note instanceof NoteProxyWithBasicNote) {
			BasicNote innerNote = ((NoteProxyWithBasicNote) note).getBasicNote();
			if (innerNote instanceof InMemoryBasicNote) {
				user = ((InMemoryBasicNote) innerNote).owner;
			}
		} else {
			user = userCache.getUser(userNoteDao.findNoteOwner(note.getNoteId()));
		}
		return user;
	}

	@Override
	public OperationDelegatingNote getNote(long noteId) {
		OperationDelegatingNote note = noteCache.getNoteIfLoaded(noteId);
		if (note == null) {
			note = noteCache.getNote(userNoteDao.findNote(noteId));
		}
		return note;
	}

	@Override
	public void publishComment(long noteId, CommentPublishAttributes commentAttributes, MobileNumber userId) {
		userNoteDao.createComment(noteId, commentAttributes, userId);
		Object note = noteCache.getNote(noteId);
		if (note != null && (note instanceof UserNoteInteractionAware)) {
			((UserNoteInteractionAware)note).fireAddComment(commentAttributes.getRating());
		}
	}

	@Override
	public void likeNote(long noteId, MobileNumber userId) {
		userNoteDao.saveUserLikeNote(noteId, userId);
		Object note = noteCache.getNote(noteId);
		if (note != null && (note instanceof UserNoteInteractionAware)) {
			((UserNoteInteractionAware)note).fireAddLike();
		}		
	}
	
	void watchUser(MobileNumber fanId, OperationDelegatingMobileUser user) {
		userNoteDao.addWatchConnection(fanId, user.getMobileNumber());
		userCache.getUserIfLoaded(user.getMobileNumber()).fireWatchUser();
	}

	private class InnerNoteIterator implements Iterator<BasicNote> {
		Iterator<BasicNoteDto> noteDtoIte;
		
		InnerNoteIterator(List<BasicNoteDto> noteDtos) {
			this.noteDtoIte = noteDtos.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return noteDtoIte.hasNext();
		}
		
		@Override
		public BasicNote next() {
			return noteCache.getNote(noteDtoIte.next());
		}
	}
}

