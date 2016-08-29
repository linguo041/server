package com.duoshouji.server.internal.core;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.dao.NoteDto;
import com.duoshouji.server.service.dao.RegisteredUserDto;
import com.duoshouji.server.service.dao.UserNoteDao;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteFilter;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.user.BasicUserAttributes;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserAlreadyExistsException;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.IndexRange;
import com.duoshouji.server.util.MessageProxyFactory;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.UserMessageProxy;

@Service
public class UserNoteOperationManager implements UserRepository, NoteRepository {
	
	private UserNoteDao userNoteDao;
	private MessageProxyFactory messageProxyFactory;

	@Autowired
	private UserNoteOperationManager(UserNoteDao userNoteDao,
			MessageProxyFactory messageProxyFactory) {
		super();
		this.userNoteDao = userNoteDao;
		this.messageProxyFactory = messageProxyFactory;
	}

	@Override
	public RegisteredUser findUser(MobileNumber mobileNumber) {
		return get(userNoteDao.findUser(mobileNumber));
	}
	
	private OperationDelegatingMobileUser get(RegisteredUserDto userDto) {
		OperationDelegatingMobileUser user = null;
		if (userDto != null) {
			user = convert(userDto);
		}
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
	public NoteCollection findNotes() {
		return findNotes(-1l);
	}

	@Override
	public NoteCollection findNotes(long tagId) {
		NoteFilter noteFilter = null;
		if (tagId >= 0) {
			noteFilter.
		}
		return new OperationDelegatingNoteCollection(this, System.currentTimeMillis(), noteFilter);
	}

	private OperationDelegatingNote newNote(NoteDto noteDto) {
		return new OperationDelegatingNote(noteDto, this);
	}

	Iterator<Note> findNotes(long cutoff, IndexRange range, NoteFilter filter) {
		return new InnerNoteIterator(userNoteDao.findNotes(cutoff, range, filter));
	}
	
	UserMessageProxy getMessageProxy(
			OperationDelegatingMobileUser user) {
		return messageProxyFactory.getMessageProxy(user);
	}

	void logout(OperationDelegatingMobileUser user) {
		userNoteDao.removeToken(user.getMobileNumber());
	}

	String login(OperationDelegatingMobileUser user) {
		final String token = UUID.randomUUID().toString();
		userNoteDao.saveToken(user.getMobileNumber(), token);
		return token;
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

	public NoteCollection getPublishedNotes(OperationDelegatingMobileUser user) {
		return new OperationDelegatingNoteCollection(this, System.currentTimeMillis(), new NoteFilter(user.getMobileNumber()));
	}

	public long publishNote(NotePublishAttributes noteAttributes,
			OperationDelegatingMobileUser user) {
		return userNoteDao.createNote(user.getMobileNumber(), noteAttributes);
	}
	
	public BasicUserAttributes getOwner(OperationDelegatingNote note) {
		InMemoryBasicUserAttributes userAttributes = new InMemoryBasicUserAttributes(note.noteDto.owner.mobileNumber);
		userAttributes.nickname = note.noteDto.owner.nickname;
		userAttributes.portrait = note.noteDto.owner.portrait;
		return new MobileUserProxy(userAttributes, this);
	}
	
	public void loadRegisteredUser(MobileUserProxy mobileUserProxy) {
		final RegisteredUserDto userDto = userNoteDao.findUser(mobileUserProxy.getMobileNumber());
		if (userDto == null) {
			throw new UnsupportedOperationException("Can't load user.");
		}
		mobileUserProxy.delegator = convert(userDto);
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

