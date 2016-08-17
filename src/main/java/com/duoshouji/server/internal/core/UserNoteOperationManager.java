package com.duoshouji.server.internal.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.duoshouji.server.internal.dao.InMemoryRegisteredUserDto;
import com.duoshouji.server.internal.dao.NoteDto;
import com.duoshouji.server.internal.dao.RegisteredUserDto;
import com.duoshouji.server.internal.dao.UserNoteDao;
import com.duoshouji.server.service.note.CommentCollection;
import com.duoshouji.server.service.note.LikeCollection;
import com.duoshouji.server.service.note.NoteAlbum;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserAlreadyExistsException;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;

public class UserNoteOperationManager implements UserRepository, NoteRepository {
	
	private UserNoteDao userNoteDao;

	@Required
	@Autowired
	public void setUserNoteDao(UserNoteDao userNoteDao) {
		this.userNoteDao = userNoteDao;
	}

	boolean verifyPassword(OperationDelegatingMobileUser user, Password password) {
		return user.getPasswordDigest().equals(password.toString());
	}

	public void setPassword(OperationDelegatingMobileUser user, Password password) {
		user.setPasswordDigest(password.toString());
	}

	@Override
	public RegisteredUser findUser(MobileNumber mobileNumber) {
		return findUser(new UserIdentifier(mobileNumber));
	}
	
	@Override
	public RegisteredUser createUser(MobileNumber mobileNumber) {
		final UserIdentifier userId = new UserIdentifier(mobileNumber);
		if (containsUser(userId)) {
			throw new UserAlreadyExistsException("User already exists in system, user id: " + userId);
		}
		OperationDelegatingMobileUser user = new OperationDelegatingMobileUser(
				new InMemoryRegisteredUserDto(userId, mobileNumber), this);
		userNoteDao.addUser(userId, mobileNumber);
		return user;
	}
	
	private boolean containsUser(UserIdentifier userId) {
		return findUser(userId) != null;
	}
	
	public RegisteredUser findUser(UserIdentifier userId) {
		RegisteredUser user = null;
		RegisteredUserDto userDto = userNoteDao.findUser(userId);
		if (userDto != null) {
			user = new OperationDelegatingMobileUser(userDto, this); 
		}
		return user;
	}
	
	@Override
	public NoteCollection findNotes() {
		return new OperationDelegatingNoteCollection(userNoteDao.findNotes(), this);
	}

	NoteAlbum getNoteAlbum(OperationDelegatingNote operationDelegatingNote) {
		return new DtoBasedNoteAlbum(userNoteDao.findNoteAlbum(operationDelegatingNote.noteDto));
	}

	RegisteredUser getOwner(OperationDelegatingNote operationDelegatingNote) {
		return new OperationDelegatingMobileUser(userNoteDao.getOwner(operationDelegatingNote.noteDto), this);
	}

	LikeCollection getLikes(OperationDelegatingNote operationDelegatingNote) {
		final int size = userNoteDao.getLikes(operationDelegatingNote.noteDto).size();
		return new LikeCollection() {
			@Override
			public int size() {
				return size;
			}
		};
	}

	CommentCollection getComments(OperationDelegatingNote operationDelegatingNote) {
		final int size = userNoteDao.getComments(operationDelegatingNote.noteDto).size();
		return new CommentCollection() {
			@Override
			public int size() {
				return size;
			}
		};
	}

	OperationDelegatingNote newNote(NoteDto noteDto) {
		return new OperationDelegatingNote(noteDto, this);
	}
}
