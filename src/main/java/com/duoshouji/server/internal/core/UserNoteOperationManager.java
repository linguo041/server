package com.duoshouji.server.internal.core;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoshouji.server.internal.dao.InMemoryRegisteredUserDto;
import com.duoshouji.server.internal.dao.NoteDto;
import com.duoshouji.server.internal.dao.NoteDtoCollection;
import com.duoshouji.server.internal.dao.RegisteredUserDto;
import com.duoshouji.server.internal.dao.UserNoteDao;
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
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;

@Service
public class UserNoteOperationManager implements UserRepository, NoteRepository {
	
	private UserNoteDao userNoteDao;

	@Autowired
	public UserNoteOperationManager(UserNoteDao userNoteDao) {
		super();
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
}
