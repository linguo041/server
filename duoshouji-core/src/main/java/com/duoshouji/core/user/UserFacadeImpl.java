package com.duoshouji.core.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import com.duoshouji.core.FullFunctionalUser;
import com.duoshouji.core.MessageProxy;
import com.duoshouji.core.MessageProxyFactory;
import com.duoshouji.core.SecureAccessFacade;
import com.duoshouji.service.user.Gender;
import com.duoshouji.service.user.Password;
import com.duoshouji.service.user.PasswordNotSetException;
import com.duoshouji.service.user.UserFacade;
import com.duoshouji.service.user.UserNotFoundException;
import com.duoshouji.service.user.UserProfile;
import com.duoshouji.service.util.Image;
import com.duoshouji.service.util.MobileNumber;
import com.duoshouji.service.util.VerificationCode;

@Service
public class UserFacadeImpl implements UserFacade {
	
	private MessageProxyFactory messageProxyFactory;
	private SecureAccessFacade secureAccessFacade;
	private UserRepository userRepository;
	
	@Required
	@Autowired
	public void setMessageProxyFactory(MessageProxyFactory messageProxyFactory) {
		this.messageProxyFactory = messageProxyFactory;
	}

	@Required
	@Autowired
	public void setSecureAccessFacade(SecureAccessFacade secureAccessFacade) {
		this.secureAccessFacade = secureAccessFacade;
	}

	@Required
	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public void sendLoginVerificationCode(MobileNumber mobileNumber) {
		long userId = getUserId(mobileNumber);
		if (userId == NULL_USER_ID) {
			userId = registerUser(mobileNumber);
		}
		secureAccessFacade.getSecureChecker(userId).sendVerificationCode(messageProxyFactory.getMessageProxy(mobileNumber));
	}

	private long registerUser(MobileNumber mobileNumber) {
		final FullFunctionalUser user = userRepository.createUser(mobileNumber);
		activateFollows(user);
		return user.getUserId();
	}

	private void activateFollows(FullFunctionalUser invitee) {
		List<FullFunctionalUser> inviters = invitee.getInviters();
		for (FullFunctionalUser inviter : inviters) {
			inviter.follow(invitee.getUserId());
		}
	}

	@Override
	public long verificationCodeLogin(MobileNumber mobileNumber, VerificationCode verificationCode) {
		final long userId = getUserId(mobileNumber);
		if (userId == NULL_USER_ID) {
			throw new UserNotFoundException(mobileNumber);
		}
		if (secureAccessFacade.getSecureChecker(userId).verify(verificationCode)) {
			return userId;
		} else {
			return NULL_USER_ID;
		}
	}

	@Override
	public long passwordLogin(MobileNumber mobileNumber, Password password) {		
		final FullFunctionalUser user = userRepository.getUser(mobileNumber);
		if (user == null) {
			throw new UserNotFoundException(mobileNumber);
		}
		if (!user.isPasswordSet()) {
			throw new PasswordNotSetException(user.getUserId());
		}
		if (user.getPasswordDigest().equals(password.toString())) {
			return user.getUserId();
		} else {
			return NULL_USER_ID;
		}
	}

	@Override
	public long getUserId(MobileNumber mobileNumber) {
		final FullFunctionalUser user = userRepository.getUser(mobileNumber);
		if (user == null) {
			return NULL_USER_ID;
		} else {
			return user.getUserId();
		}
	}

	@Override
	public UserProfile getUserProfile(long userId) {
		return userRepository.getUser(userId);
	}

	@Override
	public UserProfileSetter newUserProfileSetter(long userId) {
		return new InnerUserProfileSetter(userRepository.getUser(userId));
	}

	@Override
	public void setPortrait(long userId, Image image) {
		userRepository.getUser(userId).setPortrait(image);
	}

	@Override
	public void sendResetPasswordVerificationCode(long userId) {
		final MessageProxy messageProxy = messageProxyFactory.getMessageProxy(userRepository.getUser(userId).getMobileNumber());
		secureAccessFacade.getSecureChecker(userId).sendVerificationCode(messageProxy);
	}

	@Override
	public boolean resetPassword(long userId, VerificationCode code, Password password) {
		final boolean verified = secureAccessFacade.getSecureChecker(userId).verify(code);
		if (verified) {
			userRepository.getUser(userId).setPassword(password);
		}
		return verified;
	}

	@Override
	public void buildFollowConnection(long followerId, long followedId) {
		userRepository.getUser(followerId).follow(followedId);
	}

	@Override
	public void inviteFriends(long userId, MobileNumber[] mobileNumbers) {
		final FullFunctionalUser user = userRepository.getUser(userId);
		for (MobileNumber mobileNumber : mobileNumbers) {
			messageProxyFactory.getMessageProxy(mobileNumber).sendInvitationMessage(user.getMobileNumber());
			user.fireInviteFriend(mobileNumber);
		}
	}

	private class InnerUserProfileSetter implements UserProfileSetter {

		private FullFunctionalUser user;
		
		private InnerUserProfileSetter(FullFunctionalUser user) {
			this.user = user;
		}

		@Override
		public void setNickname(String nickname) {
			user.setNickname(nickname);
		}

		@Override
		public void setGender(Gender gender) {
			user.setGender(gender);
		}

		@Override
		public void commitProfileChanges() {
		}
	}
}
