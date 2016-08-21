package com.duoshouji.server.internal.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duoshouji.server.service.executor.SecureAccessFacade;
import com.duoshouji.server.service.executor.SecureChecker;
import com.duoshouji.server.service.note.NoteCollection;
import com.duoshouji.server.service.note.NotePublishAttributes;
import com.duoshouji.server.service.user.NoteBuilder;
import com.duoshouji.server.service.user.RegisteredUser;
import com.duoshouji.server.service.user.UserFacade;
import com.duoshouji.server.service.user.UserNotExistsException;
import com.duoshouji.server.service.user.UserRepository;
import com.duoshouji.server.util.MobileNumber;
import com.duoshouji.server.util.Password;
import com.duoshouji.server.util.VerificationCode;

@Service
public class UserFacadeImpl implements UserFacade {

	private UserRepository userRepository;
	private SecureAccessFacade secureAccessFacade;
	
	@Autowired
	private UserFacadeImpl(UserRepository userRepository,
			SecureAccessFacade secureAccessFacade) {
		super();
		this.userRepository = userRepository;
		this.secureAccessFacade = secureAccessFacade;
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
