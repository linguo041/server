package com.duoshouji.server.internal.core;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.dao.BasicUserDto;
import com.duoshouji.server.service.dao.NoteDto;
import com.duoshouji.server.service.dao.RegisteredUserDto;
import com.duoshouji.server.service.dao.UserNoteDao;
import com.duoshouji.server.service.interaction.UserNoteInteraction;
import com.duoshouji.server.service.note.BasicNote;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteFilter;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.user.BasicUser;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.Image;
import com.duoshouji.server.util.IndexRange;
import com.duoshouji.server.util.MessageProxyFactory;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.UserMessageProxy;

@Service
public class UserNoteOperationManager implements UserRepository, NoteRepository, UserNoteInteraction {

	private UserCache userCache = new UserCache();
	private UserNoteDao userNoteDao;
	private MessageProxyFactory messageProxyFactory;

	private final class UserCache {
		
		private UniqueObjectCache cache = new HashMapUniqueObjectCache();
		
		private RegisteredUser getUser(MobileNumber mobileNumber) {
			RegisteredUser user = cache.get(mobileNumber, RegisteredUser.class);
			if (user == null) {
				user = new MobileNumberUserProxy(mobileNumber, UserNoteOperationManager.this);
				cache.put(mobileNumber, user);
			}
			return user;
		}
		
		private RegisteredUser getUser(BasicUserDto basicUserDto) {
			RegisteredUser user = cache.get(basicUserDto.mobileNumber, RegisteredUser.class);
			if (user instanceof MobileNumberUserProxy) {
				InMemoryBasicUser userAttributes = new InMemoryBasicUser(basicUserDto.mobileNumber);
				userAttributes.nickname = basicUserDto.nickname;
				userAttributes.portrait = basicUserDto.portrait;
				user = new BasicUserProxy(userAttributes, UserNoteOperationManager.this);
				cache.put(user.getMobileNumber(), user);
			}
			return user;

		}
		
		private RegisteredUser getUser(RegisteredUserDto userDto) {
			RegisteredUser user = cache.get(userDto.mobileNumber, RegisteredUser.class);
			if (!(user instanceof OperationDelegatingMobileUser)) {
				user = new OperationDelegatingMobileUser(userDto.mobileNumber, UserNoteOperationManager.this);
				((OperationDelegatingMobileUser)user).passwordDigest = userDto.passwordDigest;
				((OperationDelegatingMobileUser)user).nickname = userDto.nickname;
				((OperationDelegatingMobileUser)user).portrait = userDto.portrait;
				cache.put(user.getMobileNumber(), user);				
			}
			return user;
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
	
	OperationDelegatingMobileUser loadUserIfNotExists(MobileNumber mobileNumber) {
		RegisteredUser user = userCache.getUser(mobileNumber);
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
	public RegisteredUser findUser(MobileNumber mobileNumber) {
		return userCache.getUser(mobileNumber);
	}
	
	@Override
	public NoteCollection listNotes(NoteFilter noteFilter) {
		return new FilteredNoteCollection(this, System.currentTimeMillis(), noteFilter);
	}

	private OperationDelegatingNote newNote(NoteDto noteDto) {
		return new OperationDelegatingNote(this, noteDto);
	}

	Iterator<BasicNote> findNotes(long cutoff, IndexRange range, NoteFilter filter) {
		return new InnerNoteIterator(userNoteDao.findNotes(cutoff, range, filter));
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
		userNoteDao.saveUserProfile(user.getMobileNumber(), nickname);
	}

	void setPassword(OperationDelegatingMobileUser user, Password password) {
		userNoteDao.savePasswordDigest(user.getMobileNumber(), password.toString());
	}

	void setPortrait(OperationDelegatingMobileUser user, Image portrait) {
		userNoteDao.savePortrait(user.getMobileNumber(), portrait);
	}

	public void setMainImage(OperationDelegatingNote note, Image mainImage) {
		userNoteDao.saveNoteImage(note.getNoteId(), mainImage);
	}
	
	@Override
	public NoteCollection getUserPublishedNotes(BasicUser user) {
		return new UserPublishedNoteCollection(this, System.currentTimeMillis(), user.getMobileNumber());
	}
	
	@Override
	public long publishNote(BasicUser user, NotePublishAttributes noteAttributes) {
		noteAttributes.checkAttributesSetup();
		return userNoteDao.createNote(user.getMobileNumber(), noteAttributes);
	}
	
	@Override
	public BasicUser getOwner(BasicNote note) {
		BasicUser user = null;
		if (note instanceof OperationDelegatingNote) {
			user = userCache.getUser(((OperationDelegatingNote) note).noteDto.owner);
		} else {
			user = userCache.getUser(userNoteDao.findNoteOwner(note.getNoteId()));
		}
		return user;
	}

	@Override
	public Note getNote(long noteId) {
		return newNote(userNoteDao.findNote(noteId));
	}

	private class InnerNoteIterator implements Iterator<BasicNote> {
		Iterator<NoteDto> noteDtoIte;
		
		InnerNoteIterator(List<NoteDto> noteDtos) {
			this.noteDtoIte = noteDtos.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return noteDtoIte.hasNext();
		}
		
		@Override
		public BasicNote next() {
			return newNote(noteDtoIte.next());
		}
	}
}

