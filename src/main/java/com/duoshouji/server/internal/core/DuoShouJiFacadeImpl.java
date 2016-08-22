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
	public String verificationCodeLogin(MobileNumber mobileNumber, VerificationCode verificationCode) {
		final RegisteredUser user = getUser(mobileNumber);
		SecureChecker checker = secureAccessFacade.getSecureChecker(user);
		if (checker.verify(verificationCode)) {
			return user.login();
		} else {
			return null;
		}
	}

	@Override
	public String passwordLogin(MobileNumber mobileNumber, Password password) {
		final RegisteredUser user = getUser(mobileNumber);
		if (user.verifyPassword(password)) {
			return user.login();
		} else {
			return null;
		}

	}
	
	private RegisteredUser getUser(MobileNumber mobileNumber) {
		RegisteredUser user = userRepository.findUser(mobileNumber);
		if (user == null) {
			throw new UserNotExistsException("Mobile: " + mobileNumber);
		}
		return user;
	}

	@Override
	public boolean resetPassword(String userToken, VerificationCode verificationCode,
			Password password) {
		final RegisteredUser user = userRepository.getUser(userToken);
		boolean isSuccess = false;
		if (secureAccessFacade.getSecureChecker(user).verify(verificationCode)) {
			user.setPassword(password);
			isSuccess = true;
		}
		return isSuccess;
	}

	@Override
	public void sendResetPasswordVerificationCode(String userToken) {
		final RegisteredUser user = userRepository.getUser(userToken);
		secureAccessFacade.getSecureChecker(user).sendVerificationCode();
	}

	@Override
	public void updateNickname(String userToken, String nickname) {
		userRepository.getUser(userToken).setNickname(nickname);		
	}

	@Override
	public NoteBuilder newNotePublisher(String userToken) {
		return new InnerNoteBuilder(userRepository.getUser(userToken));
	}

	@Override
	public NoteCollection getUserPublishedNotes(String userToken) {
		return userRepository.getUser(userToken).getPublishedNotes();
	}
	
	@Override
	public void logout(String userToken) {
		userRepository.getUser(userToken).logout();
	}
	
	@Override
	public NoteCollection pushSquareNotes(String userToken) {
		return pushSquareNotes(userRepository.getUser(userToken));
	}
	
	@Override
	public NoteCollection getPushedSquareNotes(String userToken) {
		final RegisteredUser user = userRepository.getUser(userToken);
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
		public void setTag(String tag) {
			valueHolder.setTag(tag);
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
