package com.duoshouji.server.internal.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.DuoShouJiFacade;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.service.note.NoteRepository;
import com.duoshouji.server.service.user.NoteBuilder;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserNotExistsException;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.service.verify.SecureAccessFacade;
import com.duoshouji.server.service.verify.SecureChecker;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@Service
public class DuoShouJiFacadeImpl implements DuoShouJiFacade {
	
	private NoteRepository noteRepository;
	private UserRepository userRepository;
	private NoteCollectionSnapshots snapshots;
	private SecureAccessFacade secureAccessFacade;
	
	@Autowired
	private DuoShouJiFacadeImpl(UserRepository userRepository,
			SecureAccessFacade secureAccessFacade, NoteRepository noteRepository) {
		super();
		this.userRepository = userRepository;
		this.secureAccessFacade = secureAccessFacade;
		this.noteRepository = noteRepository;
		snapshots = new NoteCollectionSnapshots();
	}
	
	@Override
	public void sendLoginVerificationCode(MobileNumber mobileNumber) {
		RegisteredUser user = userRepository.findUser(mobileNumber);
		if (user == null) {
			user = userRepository.createUser(mobileNumber);
		}
		secureAccessFacade.getSecureChecker(user).sendVerificationCode();
	}

	@Override
	public boolean verificationCodeLogin(MobileNumber mobileNumber, VerificationCode verificationCode) {
		final RegisteredUser user = getUser(mobileNumber);
		SecureChecker checker = secureAccessFacade.getSecureChecker(user);
		return checker.verify(verificationCode);
	}

	@Override
	public boolean passwordLogin(MobileNumber mobileNumber, Password password) {
		final RegisteredUser user = getUser(mobileNumber);
		return user.verifyPassword(password);
	}
	
	private RegisteredUser getUser(MobileNumber mobileNumber) {
		RegisteredUser user = userRepository.findUser(mobileNumber);
		if (user == null) {
			throw new UserNotExistsException("Mobile: " + mobileNumber);
		}
		return user;
	}

	@Override
	public boolean resetPassword(MobileNumber accountId, VerificationCode verificationCode,
			Password password) {
		final RegisteredUser user = getUser(accountId);
		boolean isSuccess = false;
		if (secureAccessFacade.getSecureChecker(user).verify(verificationCode)) {
			user.setPassword(password);
			isSuccess = true;
		}
		return isSuccess;
	}

	@Override
	public void sendResetPasswordVerificationCode(MobileNumber accountId) {
		final RegisteredUser user = getUser(accountId);
		secureAccessFacade.getSecureChecker(user).sendVerificationCode();
	}

	@Override
	public void updateNickname(MobileNumber accountId, String nickname) {
		getUser(accountId).setNickname(nickname);		
	}

	@Override
	public NoteBuilder newNotePublisher(MobileNumber accountId) {
		return new InnerNoteBuilder(getUser(accountId));
	}

	@Override
	public NoteCollection getUserPublishedNotes(MobileNumber accountId) {
		return getUser(accountId).getPublishedNotes();
	}
	
	@Override
	public void logout(MobileNumber accountId) {
		getUser(accountId).logout();
	}
	
	@Override
	public NoteCollection pushSquareNotes(MobileNumber accountId) {
		return pushSquareNotes(getUser(accountId));
	}
	
	@Override
	public NoteCollection getPushedSquareNotes(MobileNumber accountId) {
		final RegisteredUser user = getUser(accountId);
		NoteCollection notes = snapshots.getSnapshot(user);
		if (notes == null) {
			notes = pushSquareNotes(user);
		}
		return notes;
	}
	
	private NoteCollection pushSquareNotes(RegisteredUser user) {
		NoteCollection notes = noteRepository.findNotes();
		snapshots.putSnapshot(user, notes);
		return notes;
	}
	
	private class InnerNoteBuilder implements NoteBuilder {
		
		private RegisteredUser user;
		private long noteId = -1;
		private NotePublishAttributes valueHolder = new NotePublishAttributes();
		
		private InnerNoteBuilder(RegisteredUser user) {
			this.user = user;
		}

		@Override
		public void setTitle(String title) {
			valueHolder.setTitle(title);
		}

		@Override
		public void setContent(String content) {
			valueHolder.setContent(content);
		}

		@Override
		public long publishNote() {
			if (noteId < 0) {
				noteId = user.publishNote(valueHolder);
			}
			return noteId;
		}
	}
}
