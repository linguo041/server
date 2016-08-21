package com.duoshouji.server.internal.core;

import java.util.Iterator;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoshouji.server.internal.dao.InMemoryRegisteredUserDto;
import com.duoshouji.server.service.dao.NoteDto;
import com.duoshouji.server.service.dao.NoteDtoCollection;
import com.duoshouji.server.service.dao.RegisteredUserDto;
import com.duoshouji.server.service.dao.UserNoteDao;
import com.duoshouji.server.service.note.CommentCollection;
import com.duoshouji.server.service.note.LikeCollection;
import com.duoshouji.server.service.note.Note;
import com.duoshouji.server.service.note.NoteAlbum;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserAlreadyExistsException;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.service.user.UserRepository;
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

	boolean verifyPassword(OperationDelegatingMobileUser user, Password password) {
		return user.getPasswordDigest().equals(password.toString());
	}

	@Override
	public RegisteredUser findUser(MobileNumber mobileNumber) {
		RegisteredUser user = null;
		RegisteredUserDto userDto = userNoteDao.findUser(userId);
		if (userDto != null) {
			user = new OperationDelegatingMobileUser(userDto, this); 
		}
		return user;
	}
	
	@Override
	public RegisteredUser createUser(MobileNumber mobileNumber) {
		if (containsUser(mobileNumber)) {
			throw new UserAlreadyExistsException("User already exists in system, user id: " + userId);
		}
		OperationDelegatingMobileUser user = new OperationDelegatingMobileUser(
				new InMemoryRegisteredUserDto(userId, mobileNumber), this);
		userNoteDao.addUser(mobileNumber);
		return user;
	}
	
	private boolean containsUser(MobileNumber mobileNumber) {
		return findUser(mobileNumber) != null;
	}
	
	@Override
	public NoteCollection findNotes() {
		return new OperationDelegatingNoteCollection(this, System.currentTimeMillis());
	}

	NoteAlbum getNoteAlbum(OperationDelegatingNote operationDelegatingNote) {
		return new DtoBasedNoteAlbum(userNoteDao.findNoteAlbum(operationDelegatingNote.noteDto));
	}

	RegisteredUser getOwner(OperationDelegatingNote operationDelegatingNote) {
		return new OperationDelegatingMobileUser(userNoteDao.findOwner(operationDelegatingNote.noteDto), this);
	}

	LikeCollection getLikes(OperationDelegatingNote operationDelegatingNote) {
		final int size = userNoteDao.findLikes(operationDelegatingNote.noteDto).size();
		return new LikeCollection() {
			@Override
			public int size() {
				return size;
			}
		};
	}

	CommentCollection getComments(OperationDelegatingNote operationDelegatingNote) {
		final int size = userNoteDao.findComments(operationDelegatingNote.noteDto).size();
		return new CommentCollection() {
			@Override
			public int size() {
				return size;
			}
		};
	}

	private OperationDelegatingNote newNote(NoteDto noteDto) {
		return new OperationDelegatingNote(noteDto, this);
	}

	Iterator<Note> findNotes(long cutoff) {
		return new InnerNoteIterator(userNoteDao.findNotes(cutoff));
	}

	Iterator<Note> findNotes(long cutoff, int startIndex, int endIndex) {
		return new InnerNoteIterator(userNoteDao.findNotes(cutoff, startIndex, endIndex));
	}
	
	private class InnerNoteIterator implements Iterator<Note> {
		Iterator<NoteDto> noteDtoIte;
		
		InnerNoteIterator(NoteDtoCollection noteDtos) {
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

	@Override
	public RegisteredUser getUser(String token) {
		RegisteredUser user = null;
		RegisteredUserDto userDto = userNoteDao.findUser(token);
		if (userDto != null) {
			user = new OperationDelegatingMobileUser(userDto, this); 
		}
		return user;

	}

	UserMessageProxy getMessageProxy(
			OperationDelegatingMobileUser user) {
		return messageProxyFactory.getMessageProxy(user);
	}

	void logout(OperationDelegatingMobileUser user) {
		userNoteDao.removeToken(user.getIdentifier());
	}

	String login(OperationDelegatingMobileUser user) {
		final String token = UUID.randomUUID().toString();
		userNoteDao.saveToken(user.getIdentifier(), token);
		return token;
	}

	void setNickname(OperationDelegatingMobileUser user, String nickname) {
		userNoteDao.saveUserProfile(user.getIdentifier(), nickname);
	}
}
