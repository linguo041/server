package com.duoshouji.server.internal.core;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.dao.NoteDto;
import com.duoshouji.server.service.dao.RegisteredUserDto;
import com.duoshouji.server.service.dao.UserNoteDao;
import com.duoshouji.server.service.interaction.UserNoteInteraction;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteFilter;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.user.BasicUser;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserAlreadyExistsException;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.IndexRange;
import com.duoshouji.server.util.MessageProxyFactory;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.UserMessageProxy;

@Service
public class UserNoteOperationManager implements UserRepository, NoteRepository, UserNoteInteraction {

	private UniqueObjectCache objectCache;
	private UserNoteDao userNoteDao;
	private MessageProxyFactory messageProxyFactory;

	@Autowired
	@Required
	public void setObjectCache(UniqueObjectCache objectCache) {
		this.objectCache = objectCache;
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

	OperationDelegatingMobileUser loadUser(MobileNumber mobileNumber) {
		RegisteredUserDto userDto = userNoteDao.findUser(mobileNumber);
		if (userDto == null) {
			return null;
		}
		OperationDelegatingMobileUser user = convert(userDto);
		objectCache.put(user.getMobileNumber(), user);
		return user;
	}
	
	private OperationDelegatingMobileUser convert(RegisteredUserDto userDto) {
		OperationDelegatingMobileUser user = new OperationDelegatingMobileUser(userDto.mobileNumber, this);
		user.passwordDigest = userDto.passwordDigest;
		user.nickname = userDto.nickname;
		user.portrait = userDto.portrait;
		return user;
	}
	
	@Override
	public RegisteredUser findUser(MobileNumber mobileNumber) {
		RegisteredUser result = objectCache.get(mobileNumber, RegisteredUser.class);
		if (result == null) {
			result = loadUser(mobileNumber);
		}
		return result;
	}
	
	@Override
	public RegisteredUser createUser(MobileNumber mobileNumber) {
		if (containsUser(mobileNumber)) {
			throw new UserAlreadyExistsException("User already exists in system, mobile: " + mobileNumber);
		}
		userNoteDao.createUser(mobileNumber);
		return new OperationDelegatingMobileUser(mobileNumber, this);
	}
	
	private boolean containsUser(MobileNumber mobileNumber) {
		return findUser(mobileNumber) != null;
	}

	@Override
	public NoteCollection listNotes(NoteFilter noteFilter) {
		return new FilteredNoteCollection(this, System.currentTimeMillis(), noteFilter);
	}

	private OperationDelegatingNote newNote(NoteDto noteDto) {
		return new OperationDelegatingNote(noteDto);
	}

	Iterator<Note> findNotes(long cutoff, IndexRange range, NoteFilter filter) {
		return new InnerNoteIterator(userNoteDao.findNotes(cutoff, range, filter));
	}
	
	Iterator<Note> findNotes(long cutoff, IndexRange range, MobileNumber userId) {
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

	public void setPassword(OperationDelegatingMobileUser user, Password password) {
		userNoteDao.savePasswordDigest(user.getMobileNumber(), password.toString());
	}

	@Override
	public NoteCollection getUserPublishedNotes(BasicUser user) {
		return new UserPublishedNoteCollection(this, System.currentTimeMillis(), user.getMobileNumber());
	}
	
	public BasicUser getOwner(OperationDelegatingNote note) {
		InMemoryBasicUser userAttributes = new InMemoryBasicUser(note.noteDto.owner.mobileNumber);
		userAttributes.nickname = note.noteDto.owner.nickname;
		userAttributes.portrait = note.noteDto.owner.portrait;
		return new BasicUserProxy(userAttributes, this);
	}
	
	@Override
	public long publishNote(BasicUser user, NotePublishAttributes noteAttributes) {
		noteAttributes.checkAttributesSetup();
		return userNoteDao.createNote(user.getMobileNumber(), noteAttributes);
	}
	
	@Override
	public BasicUser getOwner(Note note) {
		return userNoteDao.getNoteOwner(note.getNoteId());
	}

	private class InnerNoteIterator implements Iterator<Note> {
		Iterator<NoteDto> noteDtoIte;
		
		InnerNoteIterator(List<NoteDto> noteDtos) {
			this.noteDtoIte = noteDtos.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return noteDtoIte.hasNext();
		}
		
		@Override
		public Note next() {
			return newNote(noteDtoIte.next());
		}
	}
}

