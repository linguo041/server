package com.duoshouji.server.internal.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.duoshouji.server.internal.dao.UserNoteDao;
import com.duoshouji.server.internal.executor.DelegatedVerificationCodeLoginExecutor;
import com.duoshouji.server.internal.executor.SmsVerificationCodeAuthenticationExecutor;
import com.duoshouji.server.internal.note.NoteDto;
import com.duoshouji.server.internal.note.NoteRepository;
import com.duoshouji.server.internal.user.RegisteredUserDto;
import com.duoshouji.server.internal.user.UserRepository;
import com.duoshouji.server.service.executor.VerificationCodeLoginExecutor;
import com.duoshouji.server.service.note.CommentCollection;
import com.duoshouji.server.service.note.LikeCollection;
import com.duoshouji.server.service.note.NoteAlbum;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserAlreadyExistsException;
import com.duoshouji.server.service.user.UserIdentifier;
import com.duoshouji.server.util.MessageProxyFactory;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCodeGenerator;

public class UserNoteOperationManager implements UserRepository, NoteRepository {
	
	private VerificationCodeGenerator codeGenerator;
	private MessageProxyFactory messageProxyFactory;
	private UserNoteDao userNoteDao;

	@Required
	@Autowired
	public void setCodeGenerator(VerificationCodeGenerator codeGenerator) {
		this.codeGenerator = codeGenerator;
	}
	
	@Required
	@Autowired
	public void setMessageProxyFactory(MessageProxyFactory messageProxyFactory) {
		this.messageProxyFactory = messageProxyFactory;
	}

	@Required
	@Autowired
	public void setUserNoteDao(UserNoteDao userNoteDao) {
		this.userNoteDao = userNoteDao;
	}

	boolean verifyPassword(OperationDelegatingMobileUser user, Password password) {
		return user.getPasswordDigest().equals(password.toString());
	}

	VerificationCodeLoginExecutor newVerificationCodeLoginExecutor(
			OperationDelegatingMobileUser user) {
		return new DelegatedVerificationCodeLoginExecutor(
				new SmsVerificationCodeAuthenticationExecutor(messageProxyFactory.getMessageProxy(user), codeGenerator), user);
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
		return new OperationDelegatingNoteCollection(noteDao.findNotes(), this);
	}

	NoteAlbum getNoteAlbum(OperationDelegatingNote operationDelegatingNote) {
		return new DtoBasedNoteAlbum(noteDao.findNoteAlbum(operationDelegatingNote.noteDto));
	}

	RegisteredUser getOwner(OperationDelegatingNote operationDelegatingNote) {
		// TODO Auto-generated method stub
		return null;
	}

	LikeCollection getLiks(OperationDelegatingNote operationDelegatingNote) {
		// TODO Auto-generated method stub
		return null;
	}

	CommentCollection getComments(OperationDelegatingNote operationDelegatingNote) {
		// TODO Auto-generated method stub
		return null;
	}

	OperationDelegatingNote newNote(NoteDto noteDto) {
		return new OperationDelegatingNote(noteDto, this);
	}
}
